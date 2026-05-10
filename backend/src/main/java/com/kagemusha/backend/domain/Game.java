package com.kagemusha.backend.domain;

public class Game {

    private Long id;
    private Board board;
    private PlayerType turn;
    private GameStatus status;

    public Game(Long id, Board board, PlayerType turn, GameStatus status) {
        this.id = id;
        this.board = board;
        this.turn = turn;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerType getTurn() {
        return turn;
    }

    public GameStatus getStatus() {
        return status;
    }
}