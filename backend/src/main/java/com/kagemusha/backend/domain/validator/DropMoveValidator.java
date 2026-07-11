package com.kagemusha.backend.domain.validator;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.Piece;
import com.kagemusha.backend.domain.PieceType;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenMove;

public class DropMoveValidator {

    private DropMoveValidator() {
    }

    public static void validate(Game game, SfenMove move) {
        if (!move.isDrop()) {
            throw new IllegalArgumentException("持ち駒打ちではありません");
        }

        PieceType pieceType = move.getDropPieceType();
        PlayerType currentTurn = game.getCurrentTurn();
        Position to = move.getTo();
        Board board = game.getBoard();

        if (move.getDropOwner() != currentTurn) {
            throw new IllegalArgumentException("現在の手番の持ち駒ではありません");
        }

        if (pieceType == PieceType.GYOKU) {
            throw new IllegalArgumentException("王は打てません");
        }

        if (board.getPiece(to) != null) {
            throw new IllegalArgumentException("打つ場所にすでに駒があります");
        }

        if (game.getCapturedPieces().count(currentTurn, pieceType) <= 0) {
            throw new IllegalArgumentException("指定された持ち駒を持っていません");
        }

        validateCannotDropOnDeadRow(currentTurn, pieceType, to);

        if (pieceType == PieceType.FU) {
            validateNoDoublePawn(game, currentTurn, to);
        }
    }

    /**
     * 歩・香・桂を、それ以上動けない段（行き所のない駒になる段）に打つことを禁止する。
     */
    private static void validateCannotDropOnDeadRow(
            PlayerType owner,
            PieceType pieceType,
            Position to
    ) {
        if (StuckPieceRule.hasNoFuture(owner, pieceType, to.getRow())) {
            throw new IllegalArgumentException("行き所のない駒になるため、その段には打てません: " + pieceType);
        }
    }

    /**
     * 二歩を禁止する。
     *
     * 同じ筋に、自分の成っていない歩がすでにある場合、
     * その筋に歩を打つことはできない。
     */
    private static void validateNoDoublePawn(Game game, PlayerType owner, Position to) {
        Board board = game.getBoard();
        int targetCol = to.getCol();

        for (int row = 1; row <= Board.SIZE; row++) {
            Position position = new Position(row, targetCol);
            Piece piece = board.getPiece(position);

            if (piece == null) {
                continue;
            }

            if (piece.getOwner() == owner
                    && piece.getType() == PieceType.FU
                    && !piece.isPromoted()) {
                throw new IllegalArgumentException("二歩です。同じ筋に歩を打つことはできません");
            }
        }
    }
}