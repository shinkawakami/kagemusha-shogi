package com.kagemusha.backend.infrastructure.messaging;

import com.kagemusha.backend.domain.FinishReason;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;

import java.util.UUID;

/**
 * WebSocket で配信する対局イベント。
 *
 * <p>種別（{@link GameEventType}）ごとに使うフィールドが異なるため、
 * コンストラクタを直接使わず種別ごとのファクトリメソッドで生成する。
 * 未使用フィールドは {@code null} になる。
 */
public record GameEvent(
        GameEventType type,
        UUID gameId,
        GameStatus status,
        PlayerType currentTurn,
        PlayerType selectedPlayer,
        String lastMove,
        PlayerType winner,
        FinishReason finishReason
) {

    /** 後手が参加した。 */
    public static GameEvent playerJoined(Game game) {
        return new GameEvent(
                GameEventType.PLAYER_JOINED,
                game.getId(), game.getStatus(), game.getCurrentTurn(),
                null, null, null, null
        );
    }

    /** 影武者が選択された。 */
    public static GameEvent shadowSelected(Game game, PlayerType selectedPlayer) {
        return new GameEvent(
                GameEventType.SHADOW_SELECTED,
                game.getId(), game.getStatus(), game.getCurrentTurn(),
                selectedPlayer, null, null, null
        );
    }

    /** 対局が開始した。 */
    public static GameEvent gameStarted(Game game) {
        return new GameEvent(
                GameEventType.GAME_STARTED,
                game.getId(), game.getStatus(), game.getCurrentTurn(),
                null, null, null, null
        );
    }

    /** 指し手が行われた。 */
    public static GameEvent move(Game game, String lastMove) {
        return new GameEvent(
                GameEventType.MOVE,
                game.getId(), game.getStatus(), game.getCurrentTurn(),
                null, lastMove, null, null
        );
    }

    /** 対局が終了した。 */
    public static GameEvent gameFinished(Game game) {
        return new GameEvent(
                GameEventType.GAME_FINISHED,
                game.getId(), game.getStatus(), game.getCurrentTurn(),
                null, null, game.getWinner(), game.getFinishReason()
        );
    }
}
