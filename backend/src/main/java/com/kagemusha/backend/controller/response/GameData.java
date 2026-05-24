package com.kagemusha.backend.controller.response;

public class GameData {
    private Long gameId;
    private String status;
    private String winner;
    private String board;
    private String move;

    public GameData(Long gameId, String status, String winner, String board, String move) {
        this.gameId = gameId;
        this.status = status;
        this.winner = winner;
        this.board = board;
        this.move = move;
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

    public String getMove() {
        return move;
    }
}
