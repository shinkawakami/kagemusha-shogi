package com.kagemusha.backend.controller.response;

public class GameResponse {
    private boolean success;
    private GameData data;

    public GameResponse(boolean success, GameData data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public GameData getData() {
        return data;
    }
}
