package com.kagemusha.backend.infrastructure.persistence;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayedMove;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenPositionConverter;
import com.kagemusha.backend.infrastructure.persistence.entity.GameEntity;
import com.kagemusha.backend.infrastructure.persistence.entity.GameMoveEntity;
import com.kagemusha.backend.infrastructure.persistence.entity.GamePlayerEntity;
import com.kagemusha.backend.infrastructure.persistence.entity.ShadowSelectionEntity;
import com.kagemusha.backend.infrastructure.persistence.repository.GameJpaRepository;
import com.kagemusha.backend.infrastructure.persistence.repository.GameMoveJpaRepository;
import com.kagemusha.backend.infrastructure.persistence.repository.GamePlayerJpaRepository;
import com.kagemusha.backend.infrastructure.persistence.repository.ShadowSelectionJpaRepository;
import com.kagemusha.backend.port.out.GameRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link GameRepository} ポートの JPA 実装（ヘキサゴナルアーキテクチャの outbound adapter）。
 *
 * <p>永続化モデル（指し手ログ {@code game_moves} を正、現在局面を投影として維持）を
 * ここに閉じ込める。指し手は追記オンリー、投影・プレイヤー・影武者は upsert する。
 */
@Repository
public class GameRepositoryAdapter implements GameRepository {

    private final GameJpaRepository gameJpa;
    private final GamePlayerJpaRepository playerJpa;
    private final GameMoveJpaRepository moveJpa;
    private final ShadowSelectionJpaRepository shadowJpa;

    public GameRepositoryAdapter(
            GameJpaRepository gameJpa,
            GamePlayerJpaRepository playerJpa,
            GameMoveJpaRepository moveJpa,
            ShadowSelectionJpaRepository shadowJpa
    ) {
        this.gameJpa = gameJpa;
        this.playerJpa = playerJpa;
        this.moveJpa = moveJpa;
        this.shadowJpa = shadowJpa;
    }

    @Override
    @Transactional
    public Game save(Game game) {
        UUID gameId = game.getId();
        OffsetDateTime now = OffsetDateTime.now();

        Optional<GameEntity> existing = gameJpa.findById(gameId);
        int persistedPly = existing.map(GameEntity::getPly).orElse(0);

        upsertGameRow(game, existing.orElse(null), now);
        insertNewMoves(game, persistedPly, now);
        upsertPlayers(game, now);
        upsertShadows(game, now);

        return game;
    }

    @Override
    @Transactional
    public int deleteAbandoned(OffsetDateTime updatedBefore) {
        // 終局済み（棋譜・戦績として保持する）は除外。子行は ON DELETE CASCADE で連鎖削除される。
        return gameJpa.deleteByStatusNotAndUpdatedAtBefore(GameStatus.FINISHED, updatedBefore);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> findById(UUID id) {
        Optional<GameEntity> gameEntity = gameJpa.findById(id);

        if (gameEntity.isEmpty()) {
            return Optional.empty();
        }

        GameEntity entity = gameEntity.get();

        Map<PlayerType, GamePlayerEntity> players = playerJpa.findByGameId(id).stream()
                .collect(Collectors.toMap(GamePlayerEntity::getPlayerType, Function.identity()));

        Map<PlayerType, ShadowSelectionEntity> shadows = shadowJpa.findByGameId(id).stream()
                .collect(Collectors.toMap(ShadowSelectionEntity::getPlayerType, Function.identity()));

        List<PlayedMove> moves = moveJpa.findByGameIdOrderByPlyAsc(id).stream()
                .map(m -> new PlayedMove(m.getPly(), m.getMoveSfen(), m.getPlayedBy()))
                .toList();

        Game game = Game.restore(
                id,
                entity.getMode(),
                entity.getStatus(),
                entity.getCurrentSfen(),
                userToken(players, PlayerType.SENTE),
                userToken(players, PlayerType.GOTE),
                shadowPosition(shadows, PlayerType.SENTE),
                shadowPosition(shadows, PlayerType.GOTE),
                entity.getWinner(),
                entity.getFinishReason(),
                moves
        );

        return Optional.of(game);
    }

    /**
     * 現在局面の投影行（games）を upsert する。
     */
    private void upsertGameRow(Game game, GameEntity existing, OffsetDateTime now) {
        GameEntity entity = existing != null ? existing : new GameEntity();

        if (existing == null) {
            entity.setId(game.getId());
            entity.setCreatedAt(now);
        }

        entity.setMode(game.getMode());
        entity.setStatus(game.getStatus());
        entity.setCurrentSfen(game.getSfen());
        entity.setPly(game.getMoves().size());
        entity.setWinner(game.getWinner());
        entity.setFinishReason(game.getFinishReason());
        entity.setUpdatedAt(now);

        if (game.getStatus() == GameStatus.FINISHED && entity.getFinishedAt() == null) {
            entity.setFinishedAt(now);
        }

        gameJpa.save(entity);
    }

    /**
     * まだ永続化されていない手だけを追記する（追記オンリー）。
     */
    private void insertNewMoves(Game game, int persistedPly, OffsetDateTime now) {
        for (PlayedMove move : game.getMoves()) {
            if (move.ply() <= persistedPly) {
                continue;
            }

            GameMoveEntity entity = new GameMoveEntity();
            entity.setId(UUID.randomUUID());
            entity.setGameId(game.getId());
            entity.setPly(move.ply());
            entity.setMoveSfen(move.moveSfen());
            entity.setPlayedBy(move.playedBy());
            entity.setPlayedAt(now);

            // 即時 flush して UNIQUE(game_id, ply) 違反をその場で顕在化させる（楽観ロック）。
            moveJpa.saveAndFlush(entity);
        }
    }

    /**
     * トークンを持つ側のプレイヤー行を必要に応じて挿入する。
     *
     * <p>トークンは設定後に変わらないため、未登録の側だけを挿入する。
     */
    private void upsertPlayers(Game game, OffsetDateTime now) {
        Map<PlayerType, GamePlayerEntity> existing = playerJpa.findByGameId(game.getId()).stream()
                .collect(Collectors.toMap(GamePlayerEntity::getPlayerType, Function.identity()));

        insertPlayerIfNeeded(game.getId(), PlayerType.SENTE, game.getSenteUserToken(), existing, now);
        insertPlayerIfNeeded(game.getId(), PlayerType.GOTE, game.getGoteUserToken(), existing, now);
    }

    private void insertPlayerIfNeeded(
            UUID gameId,
            PlayerType playerType,
            String userToken,
            Map<PlayerType, GamePlayerEntity> existing,
            OffsetDateTime now
    ) {
        if (userToken == null || existing.containsKey(playerType)) {
            return;
        }

        GamePlayerEntity entity = new GamePlayerEntity();
        entity.setId(UUID.randomUUID());
        entity.setGameId(gameId);
        entity.setPlayerType(playerType);
        entity.setUserToken(userToken);
        entity.setJoinedAt(now);

        playerJpa.save(entity);
    }

    /**
     * 影武者の選択を upsert する。
     *
     * <p>影武者の駒が動くと位置が変わるため、既存行があれば位置を更新する。
     */
    private void upsertShadows(Game game, OffsetDateTime now) {
        Map<PlayerType, ShadowSelectionEntity> existing = shadowJpa.findByGameId(game.getId()).stream()
                .collect(Collectors.toMap(ShadowSelectionEntity::getPlayerType, Function.identity()));

        upsertShadow(game.getId(), PlayerType.SENTE, game.getSenteShadowPosition(), existing, now);
        upsertShadow(game.getId(), PlayerType.GOTE, game.getGoteShadowPosition(), existing, now);
    }

    private void upsertShadow(
            UUID gameId,
            PlayerType playerType,
            Position position,
            Map<PlayerType, ShadowSelectionEntity> existing,
            OffsetDateTime now
    ) {
        if (position == null) {
            return;
        }

        String positionText = SfenPositionConverter.toSfen(position);

        ShadowSelectionEntity entity = existing.get(playerType);

        if (entity == null) {
            entity = new ShadowSelectionEntity();
            entity.setId(UUID.randomUUID());
            entity.setGameId(gameId);
            entity.setPlayerType(playerType);
            entity.setSelectedAt(now);
        }

        entity.setPosition(positionText);

        shadowJpa.save(entity);
    }

    private String userToken(Map<PlayerType, GamePlayerEntity> players, PlayerType playerType) {
        GamePlayerEntity entity = players.get(playerType);
        return entity == null ? null : entity.getUserToken();
    }

    private Position shadowPosition(Map<PlayerType, ShadowSelectionEntity> shadows, PlayerType playerType) {
        ShadowSelectionEntity entity = shadows.get(playerType);
        return entity == null ? null : SfenPositionConverter.toPosition(entity.getPosition());
    }
}
