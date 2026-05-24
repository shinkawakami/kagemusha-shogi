package com.kagemusha.backend.domain;

public class Game {

    private final Long id;
    private Board board;
    private PlayerType currentTurn;
    private GameStatus status;
    private PlayerType winner;

    public Game(Long id, Board board, PlayerType currentTurn) {
        this.id = id;
        this.board = board;
        this.currentTurn = currentTurn;
        this.status = GameStatus.PLAYING;
        this.winner = null;
    }

    public static Game createInitialGame(Long id) {
        Board board = SfenConverter.toBoard(SfenConstants.INITIAL_SFEN);
        PlayerType currentTurn = SfenConverter.extractCurrentTurn(SfenConstants.INITIAL_SFEN);

        return new Game(id, board, currentTurn);
    }

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerType getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public PlayerType getWinner() {
        return winner;
    }

    public String getSfen() {
        return SfenConverter.fromBoard(board, currentTurn);
    }

    public void switchTurn() {
        this.currentTurn = this.currentTurn.opposite();
    }

    public void finish(PlayerType winner) {
        this.status = GameStatus.FINISHED;
        this.winner = winner;
    }

    public String getBoardSfen() {
        return SfenConverter.fromBoardOnly(board);
    }
}