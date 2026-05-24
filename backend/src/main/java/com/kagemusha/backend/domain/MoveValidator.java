package com.kagemusha.backend.domain;

public class MoveValidator {

    private MoveValidator() {
    }

    public static void validate(Game game, SfenMove move) {
        Board board = game.getBoard();

        Piece movingPiece = board.getPiece(move.getFrom());

        if (movingPiece == null) {
            throw new IllegalArgumentException("移動元に駒がありません");
        }

        if (movingPiece.getOwner() != game.getCurrentTurn()) {
            throw new IllegalArgumentException("現在の手番の駒ではありません");
        }

        Piece targetPiece = board.getPiece(move.getTo());

        if (targetPiece != null && targetPiece.getOwner() == movingPiece.getOwner()) {
            throw new IllegalArgumentException("移動先に味方の駒があります");
        }

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

    /**
     * 歩
     * 先手: 上に1マス
     * 後手: 下に1マス
     */
    private static void validateFuMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        if (rowDiff != forward || colDiff != 0) {
            throw new IllegalArgumentException("歩は前に1マスだけ進めます");
        }
    }

    /**
     * 香
     * 前方向に何マスでも進める
     */
    private static void validateKyoMove(Board board, Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        if (colDiff != 0) {
            throw new IllegalArgumentException("香車は前方向にしか進めません");
        }

        if (rowDiff == 0) {
            throw new IllegalArgumentException("同じマスには移動できません");
        }

        if (rowDiff * forward <= 0) {
            throw new IllegalArgumentException("香車は前方向にしか進めません");
        }

        if (!isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    /**
     * 桂
     * 先手: 左上・右上に2つ進んで1つ横
     * 後手: 左下・右下に2つ進んで1つ横
     */
    private static void validateKeimaMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        if (rowDiff != forward * 2 || Math.abs(colDiff) != 1) {
            throw new IllegalArgumentException("桂馬は前に2マス、横に1マスの位置に進めます");
        }
    }

    /**
     * 銀
     * 前、斜め前、斜め後ろ
     */
    private static void validateGinMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        boolean canMove =
                // 前
                rowDiff == forward && colDiff == 0
                        // 斜め前
                        || rowDiff == forward && Math.abs(colDiff) == 1
                        // 斜め後ろ
                        || rowDiff == -forward && Math.abs(colDiff) == 1;

        if (!canMove) {
            throw new IllegalArgumentException("銀は前・斜め前・斜め後ろに1マス進めます");
        }
    }

    /**
     * 金
     * 前、斜め前、横、後ろ
     * 斜め後ろには進めない
     */
    private static void validateKinMove(Piece movingPiece, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        int forward = getForwardDirection(movingPiece.getOwner());

        boolean canMove =
                // 前
                rowDiff == forward && colDiff == 0
                        // 斜め前
                        || rowDiff == forward && Math.abs(colDiff) == 1
                        // 横
                        || rowDiff == 0 && Math.abs(colDiff) == 1
                        // 後ろ
                        || rowDiff == -forward && colDiff == 0;

        if (!canMove) {
            throw new IllegalArgumentException("金は前・斜め前・横・後ろに1マス進めます");
        }
    }

    /**
     * 王
     * 周囲8方向に1マス
     */
    private static void validateGyokuMove(SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        if (rowDiff == 0 && colDiff == 0) {
            throw new IllegalArgumentException("同じマスには移動できません");
        }

        if (Math.abs(rowDiff) > 1 || Math.abs(colDiff) > 1) {
            throw new IllegalArgumentException("王は周囲1マスにだけ進めます");
        }
    }

    /**
     * 飛車
     * 縦横に何マスでも進める
     */
    private static void validateHishaMove(Board board, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        if (rowDiff != 0 && colDiff != 0) {
            throw new IllegalArgumentException("飛車は縦横にしか進めません");
        }

        if (rowDiff == 0 && colDiff == 0) {
            throw new IllegalArgumentException("同じマスには移動できません");
        }

        if (!isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    /**
     * 角
     * 斜めに何マスでも進める
     */
    private static void validateKakuMove(Board board, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        if (rowDiff == 0 && colDiff == 0) {
            throw new IllegalArgumentException("同じマスには移動できません");
        }

        if (Math.abs(rowDiff) != Math.abs(colDiff)) {
            throw new IllegalArgumentException("角は斜めにしか進めません");
        }

        if (!isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    /**
     * 先手は上方向、後手は下方向
     *
     * rowは上から1〜9なので、
     * 先手の前進は row - 1
     * 後手の前進は row + 1
     */
    private static int getForwardDirection(PlayerType owner) {
        return owner == PlayerType.SENTE ? -1 : 1;
    }

    /**
     * 飛車・角・香車など、複数マス進む駒の経路チェック
     */
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