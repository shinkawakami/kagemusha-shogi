package com.kagemusha.backend.controller.response;

public class GameData {
    private Long gameId;
    private String status;
    private String winner;
    private String board;

    public GameData(Long gameId, String status, String winner, String board) {
        this.gameId = gameId;
        this.status = status;
        this.winner = winner;
        this.board = board;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getStatus() {
        return status;
    }

    public String getWinner() {
        return winner;
    }

    public String getBoard() {
        return board;
    }
}
