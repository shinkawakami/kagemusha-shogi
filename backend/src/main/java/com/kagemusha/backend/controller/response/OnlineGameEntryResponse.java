package com.kagemusha.backend.controller.response;

import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;

/**
 * オンライン対局への参加結果を表すレスポンス。
 *
 * 対局作成（作成者=先手）と対局参加（参加者=後手）の
 * どちらでも同じ構造を返す。
 */
public class OnlineGameEntryResponse {

    private Long gameId;
    private GameStatus status;
    private PlayerType playerType;

    public OnlineGameEntryResponse(
            Long gameId,
            GameStatus status,
            PlayerType playerType
    ) {
        this.gameId = gameId;
        this.status = status;
        this.playerType = playerType;
    }

    public Long getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
