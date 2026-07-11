package com.kagemusha.backend.service;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameMode;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenPositionConverter;
import com.kagemusha.backend.websocket.GameEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameService {

    private final Map<Long, Game> games = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    private final GameEventPublisher gameEventPublisher;

    public GameService(GameEventPublisher gameEventPublisher) {
        this.gameEventPublisher = gameEventPublisher;
    }

    /**
     * オフライン対局を作成する。
     *
     * オフラインは1つのブラウザで先手・後手が交互に操作する。
     * userTokenやWebSocket通知は使わない。
     */
    public Game createOfflineGame() {
        Long gameId = sequence.getAndIncrement();

        Game game = Game.createOffline(gameId);

        games.put(gameId, game);

        return game;
    }

    /**
     * オンライン対局を作成する。
     *
     * 作成者は先手になる。
     */
    public Game createOnlineGame(String userToken) {
        validateUserToken(userToken);

        Long gameId = sequence.getAndIncrement();

        Game game = Game.createOnline(gameId, userToken);

        games.put(gameId, game);

        return game;
    }

    /**
     * オンライン対局に参加する。
     *
     * 参加者は後手になる。
     * 参加後は影武者選択状態にする。
     */
    public Game joinOnlineGame(Long gameId, String userToken) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        synchronized (game) {
            if (userToken.equals(game.getSenteUserToken())) {
                throw new IllegalArgumentException("作成者自身は後手として参加できません");
            }

            game.join(userToken);
        }

        gameEventPublisher.publishPlayerJoined(game);

        return game;
    }

    /**
     * 対局を取得する。
     */
    public Game getGame(Long gameId) {
        Game game = games.get(gameId);

        if (game == null) {
            throw new IllegalArgumentException("対局が見つかりません: " + gameId);
        }

        return game;
    }

    /**
     * オフライン影武者選択。
     *
     * オフラインでは userToken がないため、
     * Controllerから PlayerType を受け取る。
     */
    public Game selectShadowOffline(
            Long gameId,
            PlayerType playerType,
            String positionText
    ) {
        Game game = getGame(gameId);
        validateOfflineGame(game);

        Position position = SfenPositionConverter.toPosition(positionText);

        synchronized (game) {
            game.selectShadow(playerType, position);
        }

        return game;
    }

    /**
     * オンライン影武者選択。
     *
     * userTokenから先手・後手を判定する。
     */
    public Game selectShadowOnline(
            Long gameId,
            String userToken,
            String positionText
    ) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        Position position = SfenPositionConverter.toPosition(positionText);

        PlayerType playerType;

        synchronized (game) {
            playerType = game.resolvePlayerType(userToken);
            game.selectShadow(playerType, position);
        }

        gameEventPublisher.publishShadowSelected(game, playerType);

        if (game.getStatus() == GameStatus.PLAYING) {
            gameEventPublisher.publishGameStarted(game);
        }

        return game;
    }

    /**
     * オフライン指し手。
     *
     * オフラインでは現在の手番のプレイヤーが指した扱いにする。
     * Game.move() 側で currentTurn を使って処理する。
     */
    public Game moveOffline(
            Long gameId,
            String moveText
    ) {
        Game game = getGame(gameId);
        validateOfflineGame(game);

        synchronized (game) {
            game.move(moveText);
        }

        return game;
    }

    /**
     * オンライン指し手。
     *
     * userTokenからプレイヤーを判定し、
     * 現在の手番と一致する場合だけ Game.move() を呼ぶ。
     */
    public Game moveOnline(
            Long gameId,
            String userToken,
            String moveText
    ) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        synchronized (game) {
            PlayerType playerType = game.resolvePlayerType(userToken);

            if (game.getCurrentTurn() != playerType) {
                throw new IllegalArgumentException("現在の手番ではありません");
            }

            game.move(moveText);
        }

        if (game.getStatus() == GameStatus.FINISHED) {
            gameEventPublisher.publishGameFinished(game);
        } else {
            gameEventPublisher.publishMove(game, moveText);
        }

        return game;
    }

    /**
     * オフライン投了。
     *
     * オフラインでは userToken がないため、
     * Controllerから投了者を受け取る。
     */
    public Game resignOffline(
            Long gameId,
            PlayerType playerType
    ) {
        Game game = getGame(gameId);
        validateOfflineGame(game);

        synchronized (game) {
            game.resign(playerType);
        }

        return game;
    }

    /**
     * オンライン投了。
     *
     * userTokenから投了者を判定する。
     */
    public Game resignOnline(
            Long gameId,
            String userToken
    ) {
        validateUserToken(userToken);

        Game game = getGame(gameId);
        validateOnlineGame(game);

        synchronized (game) {
            PlayerType playerType = game.resolvePlayerType(userToken);
            game.resign(playerType);
        }

        gameEventPublisher.publishGameFinished(game);

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
}