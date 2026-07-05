package com.kagemusha.backend.controller.request;

import com.kagemusha.backend.domain.PlayerType;

public class LoseRequest {

    private PlayerType playerType;
    private String finishReason;

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getFinishReason() {
        return finishReason;
    }
}