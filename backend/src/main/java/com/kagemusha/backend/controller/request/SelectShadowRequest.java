package com.kagemusha.backend.controller.request;

import com.kagemusha.backend.domain.PlayerType;

public class SelectShadowRequest {

    private PlayerType playerType;
    private int row;
    private int column;

    public PlayerType getPlayerType() {
        return playerType;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}