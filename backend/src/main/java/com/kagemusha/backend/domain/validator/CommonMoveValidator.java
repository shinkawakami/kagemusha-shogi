package com.kagemusha.backend.domain.validator;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.Piece;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenMove;

public class CommonMoveValidator {

    private CommonMoveValidator() {
    }

    public static void validate(Game game, com.kagemusha.backend.domain.sfen.SfenMove move) {
        validateInsideBoard(move);
        validateDifferentPosition(move);
        validateSourcePieceExists(game.getBoard(), move);
        validateCurrentTurnPiece(game, move);
        validateNoFriendlyPieceOnDestination(game.getBoard(), move);
    }

    private static void validateInsideBoard(SfenMove move) {
        Position from = move.getFrom();
        Position to = move.getTo();

        if (!from.isInsideBoard()) {
            throw new IllegalArgumentException("移動元が盤外です");
        }

        if (!to.isInsideBoard()) {
            throw new IllegalArgumentException("移動先が盤外です");
        }
    }

    private static void validateDifferentPosition(SfenMove move) {
        if (move.getFrom().equals(move.getTo())) {
            throw new IllegalArgumentException("同じマスには移動できません");
        }
    }

    private static void validateSourcePieceExists(Board board, SfenMove move) {
        Piece movingPiece = board.getPiece(move.getFrom());

        if (movingPiece == null) {
            throw new IllegalArgumentException("移動元に駒がありません");
        }
    }

    private static void validateCurrentTurnPiece(Game game, SfenMove move) {
        Piece movingPiece = game.getBoard().getPiece(move.getFrom());

        if (movingPiece.getOwner() != game.getCurrentTurn()) {
            throw new IllegalArgumentException("現在の手番の駒ではありません");
        }
    }

    private static void validateNoFriendlyPieceOnDestination(Board board, SfenMove move) {
        Piece movingPiece = board.getPiece(move.getFrom());
        Piece targetPiece = board.getPiece(move.getTo());

        if (targetPiece != null && targetPiece.getOwner() == movingPiece.getOwner()) {
            throw new IllegalArgumentException("移動先に味方の駒があります");
        }
    }
}
