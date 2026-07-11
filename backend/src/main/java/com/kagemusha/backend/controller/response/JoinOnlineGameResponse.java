package com.kagemusha.backend.controller.response;

import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;

public class JoinOnlineGameResponse {

    private Long gameId;
    private GameStatus status;
    private PlayerType playerType;

    public JoinOnlineGameResponse(
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