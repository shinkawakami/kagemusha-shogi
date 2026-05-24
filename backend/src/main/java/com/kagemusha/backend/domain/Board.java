package com.kagemusha.backend.domain;

public class Board {

    private final Piece[][] squares;

    public Board() {
        this.squares = new Piece[9][9];
    }

    public Piece getPiece(Position position) {
        return squares[position.toArrayRow()][position.toArrayCol()];
    }

    public void setPiece(Position position, Piece piece) {
        squares[position.toArrayRow()][position.toArrayCol()] = piece;
    }

    public void removePiece(Position position) {
        squares[position.toArrayRow()][position.toArrayCol()] = null;
    }

    public Piece[][] getSquares() {
        return squares;
    }

    public void movePiece(Position from, Position to) {
        Piece movingPiece = getPiece(from);

        if (movingPiece == null) {
            throw new IllegalArgumentException("移動元に駒がありません");
        }

        setPiece(to, movingPiece);
        removePiece(from);
    }
}