package com.kagemusha.backend.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Board {

    private final List<Piece> pieces;

    public Board() {
        this.pieces = createInitialPieces();
    }

    public List<Piece> getPieces() {
        return Collections.unmodifiableList(pieces);
    }

    public Optional<Piece> getPieceAt(Position position) {
        return pieces.stream()
                .filter(piece ->
                        piece.getPosition().getRow() == position.getRow()
                                && piece.getPosition().getCol() == position.getCol()
                )
                .findFirst();
    }

    private List<Piece> createInitialPieces() {
        List<Piece> pieces = new ArrayList<>();

        // =====================
        // 後手：上側
        // row=1
        // 右上が col=1、左に行くほど col が増える
        // =====================
        pieces.add(new Piece(PieceType.KYO, PlayerType.GOTE, new Position(1, 1)));
        pieces.add(new Piece(PieceType.KEIMA, PlayerType.GOTE, new Position(1, 2)));
        pieces.add(new Piece(PieceType.GIN, PlayerType.GOTE, new Position(1, 3)));
        pieces.add(new Piece(PieceType.KIN, PlayerType.GOTE, new Position(1, 4)));
        pieces.add(new Piece(PieceType.GYOKU, PlayerType.GOTE, new Position(1, 5)));
        pieces.add(new Piece(PieceType.KIN, PlayerType.GOTE, new Position(1, 6)));
        pieces.add(new Piece(PieceType.GIN, PlayerType.GOTE, new Position(1, 7)));
        pieces.add(new Piece(PieceType.KEIMA, PlayerType.GOTE, new Position(1, 8)));
        pieces.add(new Piece(PieceType.KYO, PlayerType.GOTE, new Position(1, 9)));

        // row=2
        pieces.add(new Piece(PieceType.KAKU, PlayerType.GOTE, new Position(2, 2)));
        pieces.add(new Piece(PieceType.HISHA, PlayerType.GOTE, new Position(2, 8)));

        // row=3 歩
        for (int col = 1; col <= 9; col++) {
            pieces.add(new Piece(PieceType.FU, PlayerType.GOTE, new Position(3, col)));
        }

        // =====================
        // 先手：下側
        // =====================

        // row=7 歩
        for (int col = 1; col <= 9; col++) {
            pieces.add(new Piece(PieceType.FU, PlayerType.SENTE, new Position(7, col)));
        }

        // row=8
        pieces.add(new Piece(PieceType.HISHA, PlayerType.SENTE, new Position(8, 2)));
        pieces.add(new Piece(PieceType.KAKU, PlayerType.SENTE, new Position(8, 8)));

        // row=9
        pieces.add(new Piece(PieceType.KYO, PlayerType.SENTE, new Position(9, 1)));
        pieces.add(new Piece(PieceType.KEIMA, PlayerType.SENTE, new Position(9, 2)));
        pieces.add(new Piece(PieceType.GIN, PlayerType.SENTE, new Position(9, 3)));
        pieces.add(new Piece(PieceType.KIN, PlayerType.SENTE, new Position(9, 4)));
        pieces.add(new Piece(PieceType.GYOKU, PlayerType.SENTE, new Position(9, 5)));
        pieces.add(new Piece(PieceType.KIN, PlayerType.SENTE, new Position(9, 6)));
        pieces.add(new Piece(PieceType.GIN, PlayerType.SENTE, new Position(9, 7)));
        pieces.add(new Piece(PieceType.KEIMA, PlayerType.SENTE, new Position(9, 8)));
        pieces.add(new Piece(PieceType.KYO, PlayerType.SENTE, new Position(9, 9)));

        return pieces;
    }
}