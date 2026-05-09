package com.kagemusha.backend.domain;

public class Game {
    private Long id;
    private Board board;

    public Game(Long id, Board board) {
        this.id = id;
        this.board = board;
    }

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }
}