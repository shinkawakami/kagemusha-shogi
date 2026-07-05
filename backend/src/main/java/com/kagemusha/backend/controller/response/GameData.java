package com.kagemusha.backend.controller.response;

public class GameData {
    private final Long gameId;
    private final String status;
    private final String currentTurn;
    private final String board;
    private final String winner;
    private final String finishReason;

    public GameData(
            Long gameId,
            String status,
            String currentTurn,
            String board,
            String winner,
            String finishReason
    ) {
        this.gameId = gameId;
        this.status = status;
        this.currentTurn = currentTurn;
        this.board = board;
        this.winner = winner;
        this.finishReason = finishReason;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public String getBoard() {
        return board;
    }

    public String getWinner() {
        return winner;
    }

    public String getFinishReason() {
        return finishReason;
    }
}