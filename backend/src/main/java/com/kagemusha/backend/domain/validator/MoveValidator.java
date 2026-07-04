package com.kagemusha.backend.domain.validator;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.Piece;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenMove;

public class MoveValidator {

    private MoveValidator() {
    }

    public static void validate(Game game, SfenMove move) {
        CommonMoveValidator.validate(game, move);

        Board board = game.getBoard();
        Piece movingPiece = board.getPiece(move.getFrom());

        validatePieceMove(board, movingPiece, move);
    }

    private static void validatePieceMove(Board board, Piece movingPiece, SfenMove move) {
        switch (movingPiece.getType()) {
            case FU -> validateFuMove(movingPiece, move);
            case KYO -> validateKyoMove(board, movingPiece, move);
            case KEIMA -> validateKeimaMove(movingPiece, move);
            case GIN -> validateGinMove(movingPiece, move);
            case KIN -> validateKinMove(movingPiece, move);
            case GYOKU -> validateGyokuMove(move);
            case HISHA -> validateHishaMove(board, move);
            case KAKU -> validateKakuMove(board, move);
            default -> throw new IllegalArgumentException("未対応の駒です: " + movingPiece.getType());
        }
    }

    private static void validateFuMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        if (rowDiff != forward || colDiff != 0) {
            throw new IllegalArgumentException("歩は前に1マスだけ進めます");
        }
    }

    private static void validateKyoMove(Board board, Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        if (colDiff != 0) {
            throw new IllegalArgumentException("香車は前方向にしか進めません");
        }

        if (rowDiff * forward <= 0) {
            throw new IllegalArgumentException("香車は前方向にしか進めません");
        }

        if (!isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    private static void validateKeimaMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        if (rowDiff != forward * 2 || Math.abs(colDiff) != 1) {
            throw new IllegalArgumentException("桂馬は前に2マス、横に1マスの位置に進めます");
        }
    }

    private static void validateGinMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        boolean canMove =
                rowDiff == forward && colDiff == 0
                        || rowDiff == forward && Math.abs(colDiff) == 1
                        || rowDiff == -forward && Math.abs(colDiff) == 1;

        if (!canMove) {
            throw new IllegalArgumentException("銀は前・斜め前・斜め後ろに1マス進めます");
        }
    }

    private static void validateKinMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        boolean canMove =
                rowDiff == forward && colDiff == 0
                        || rowDiff == forward && Math.abs(colDiff) == 1
                        || rowDiff == 0 && Math.abs(colDiff) == 1
                        || rowDiff == -forward && colDiff == 0;

        if (!canMove) {
            throw new IllegalArgumentException("金は前・斜め前・横・後ろに1マス進めます");
        }
    }

    private static void validateGyokuMove(SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        if (Math.abs(rowDiff) > 1 || Math.abs(colDiff) > 1) {
            throw new IllegalArgumentException("王は周囲1マスにだけ進めます");
        }
    }

    private static void validateHishaMove(Board board, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        if (rowDiff != 0 && colDiff != 0) {
            throw new IllegalArgumentException("飛車は縦横にしか進めません");
        }

        if (!isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    private static void validateKakuMove(Board board, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        if (Math.abs(rowDiff) != Math.abs(colDiff)) {
            throw new IllegalArgumentException("角は斜めにしか進めません");
        }

        if (!isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    private static int getForwardDirection(PlayerType owner) {
        return owner == PlayerType.SENTE ? -1 : 1;
    }

    private static boolean isPathClear(Board board, Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();

        int rowStep = Integer.compare(rowDiff, 0);
        int colStep = Integer.compare(colDiff, 0);

        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getCol() + colStep;

        while (currentRow != to.getRow() || currentCol != to.getCol()) {
            Position currentPosition = new Position(currentRow, currentCol);

            if (board.getPiece(currentPosition) != null) {
                return false;
            }

            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }
}