package com.kagemusha.backend.service;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameMode;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenPositionConverter;
import com.kagemusha.backend.port.GameEventPort;
import com.kagemusha.backend.port.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameEventPort gameEventPublisher;

    public GameService(
            GameRepository gameRepository,
            GameEventPort gameEventPublisher
    ) {
        this.gameRepository = gameRepository;
        this.gameEventPublisher = gameEventPublisher;
    }

    /**
     * オフライン対局を作成する。
     *
     * オフラインは1つのブラウザで先手・後手が交互に操作する。
     * userTokenやWebSocket通知は使わない。
     */
    @Transactional
    public Game createOfflineGame() {
        Game game = Game.createOffline(UUID.randomUUID());

        return gameRepository.save(game);
    }

    /**
     * オンライン対局を作成する。
     *
     * 作成者は先手になる。
     */
    @Transactional
    public Game createOnlineGame(String userToken) {
        validateUserToken(userToken);

        Game game = Game.createOnline(UUID.randomUUID(), userToken);

        return gameRepository.save(game);
    }

    /**
     * オンライン対局に参加する。
     *
     * 参加者は後手になる。
     * 参加後は影武者選択状態にする。
     */
    @Transactional
    public Game joinOnlineGame(UUID gameId, String userToken) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        if (userToken.equals(game.getSenteUserToken())) {
            throw new IllegalArgumentException("作成者自身は後手として参加できません");
        }

        game.join(userToken);
        gameRepository.save(game);

        afterCommit(() -> gameEventPublisher.publishPlayerJoined(game));

        return game;
    }

    /**
     * 対局を取得する。
     */
    @Transactional(readOnly = true)
    public Game getGame(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("対局が見つかりません: " + gameId));
    }

    /**
     * オフライン影武者選択。
     *
     * オフラインでは userToken がないため、
     * Controllerから PlayerType を受け取る。
     */
    @Transactional
    public Game selectShadowOffline(
            UUID gameId,
            PlayerType playerType,
            String positionText
    ) {
        Game game = getGame(gameId);
        validateOfflineGame(game);

        Position position = SfenPositionConverter.toPosition(positionText);

        game.selectShadow(playerType, position);
        gameRepository.save(game);

        return game;
    }

    /**
     * オンライン影武者選択。
     *
     * userTokenから先手・後手を判定する。
     */
    @Transactional
    public Game selectShadowOnline(
            UUID gameId,
            String userToken,
            String positionText
    ) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        Position position = SfenPositionConverter.toPosition(positionText);

        PlayerType playerType = game.resolvePlayerType(userToken);
        game.selectShadow(playerType, position);
        gameRepository.save(game);

        afterCommit(() -> gameEventPublisher.publishShadowSelected(game, playerType));

        if (game.getStatus() == GameStatus.PLAYING) {
            afterCommit(() -> gameEventPublisher.publishGameStarted(game));
        }

        return game;
    }

    /**
     * オフライン指し手。
     *
     * オフラインでは現在の手番のプレイヤーが指した扱いにする。
     * Game.move() 側で currentTurn を使って処理する。
     */
    @Transactional
    public Game moveOffline(
            UUID gameId,
            String moveText
    ) {
        Game game = getGame(gameId);
        validateOfflineGame(game);

        game.move(moveText);
        gameRepository.save(game);

        return game;
    }

    /**
     * オンライン指し手。
     *
     * userTokenからプレイヤーを判定し、
     * 現在の手番と一致する場合だけ Game.move() を呼ぶ。
     *
     * 並行して同じ手数への着手が来た場合は、
     * game_moves の UNIQUE(game_id, ply) 制約により後発が失敗する（楽観ロック）。
     */
    @Transactional
    public Game moveOnline(
            UUID gameId,
            String userToken,
            String moveText
    ) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        PlayerType playerType = game.resolvePlayerType(userToken);

        if (game.getCurrentTurn() != playerType) {
            throw new IllegalArgumentException("現在の手番ではありません");
        }

        game.move(moveText);
        gameRepository.save(game);

        if (game.getStatus() == GameStatus.FINISHED) {
            afterCommit(() -> gameEventPublisher.publishGameFinished(game));
        } else {
            afterCommit(() -> gameEventPublisher.publishMove(game, moveText));
        }

        return game;
    }

    /**
     * オフライン投了。
     *
     * オフラインでは userToken がないため、
     * Controllerから投了者を受け取る。
     */
    @Transactional
    public Game resignOffline(
            UUID gameId,
            PlayerType playerType
    ) {
        Game game = getGame(gameId);
        validateOfflineGame(game);

        game.resign(playerType);
        gameRepository.save(game);

        return game;
    }

    /**
     * オンライン投了。
     *
     * userTokenから投了者を判定する。
     */
    @Transactional
    public Game resignOnline(
            UUID gameId,
            String userToken
    ) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        PlayerType playerType = game.resolvePlayerType(userToken);
        game.resign(playerType);
        gameRepository.save(game);

        afterCommit(() -> gameEventPublisher.publishGameFinished(game));

        return game;
    }

    private void validateUserToken(String userToken) {
        if (userToken == null || userToken.isBlank()) {
            throw new IllegalArgumentException("userTokenが必要です");
        }
    }

    private void validateOfflineGame(Game game) {
        if (game.getMode() != GameMode.OFFLINE) {
            throw new IllegalArgumentException("オフライン対局ではありません");
        }
    }

    private void validateOnlineGame(Game game) {
        if (game.getMode() != GameMode.ONLINE) {
            throw new IllegalArgumentException("オンライン対局ではありません");
        }
    }

    /**
     * トランザクションのコミット成功後に処理を実行する。
     *
     * <p>WebSocket 通知はコミット後に行う。ロールバック時
     * （例: 楽観ロック競合）に通知が飛ぶのを防ぐ。
     * トランザクションが無い場合は即時実行する。
     */
    private void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }
}
