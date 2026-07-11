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

    /**
     * 指し手が合法かどうかを検証する。
     *
     * 共通チェックを行ったあと、駒種ごとの移動ルールを検証する。
     *
     * @param game 現在のゲーム状態
     * @param move 指し手
     */
    public static void validate(Game game, SfenMove move) {
        CommonMoveValidator.validate(game, move);

        Board board = game.getBoard();
        Piece movingPiece = board.getPiece(move.getFrom());

        validatePieceMove(board, movingPiece, move);
    }

    /**
     * 駒の種類と成り状態に応じて、移動ルールを検証する。
     *
     * @param board 盤面
     * @param movingPiece 移動する駒
     * @param move 指し手
     */
    private static void validatePieceMove(Board board, Piece movingPiece, SfenMove move) {
        if (movingPiece.isPromoted()) {
            validatePromotedPieceMove(board, movingPiece, move);
            return;
        }

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
     * 成り駒の移動ルールを検証する。
     *
     * 成歩、成香、成桂、成銀は金と同じ動き。
     * 龍は飛車 + 斜め1マス。
     * 馬は角 + 縦横1マス。
     *
     * @param board 盤面
     * @param movingPiece 移動する成り駒
     * @param move 指し手
     */
    private static void validatePromotedPieceMove(Board board, Piece movingPiece, SfenMove move) {
        switch (movingPiece.getType()) {
            case FU, KYO, KEIMA, GIN -> validateKinMove(movingPiece, move);
            case HISHA -> validatePromotedHishaMove(board, move);
            case KAKU -> validatePromotedKakuMove(board, move);
            case KIN, GYOKU -> throw new IllegalArgumentException("金・王は成れません");
            default -> throw new IllegalArgumentException("未対応の成り駒です: " + movingPiece.getType());
        }
    }

    /**
     * 歩の移動ルールを検証する。
     *
     * 歩は前に1マスだけ進める。
     *
     * @param movingPiece 移動する歩
     * @param move 指し手
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
     * 香車の移動ルールを検証する。
     *
     * 香車は前方向に何マスでも進める。
     * ただし、途中に駒がある場合は進めない。
     *
     * @param board 盤面
     * @param movingPiece 移動する香車
     * @param move 指し手
     */
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

    /**
     * 桂馬の移動ルールを検証する。
     *
     * 桂馬は前に2マス、横に1マスの位置へ進める。
     *
     * @param movingPiece 移動する桂馬
     * @param move 指し手
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
     * 銀の移動ルールを検証する。
     *
     * 銀は前、斜め前、斜め後ろに1マス進める。
     *
     * @param movingPiece 移動する銀
     * @param move 指し手
     */
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

    /**
     * 金の移動ルールを検証する。
     *
     * 金は前、斜め前、横、後ろに1マス進める。
     * 斜め後ろには進めない。
     *
     * @param movingPiece 移動する金、または金と同じ動きをする成り駒
     * @param move 指し手
     */
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

    /**
     * 王の移動ルールを検証する。
     *
     * 王は周囲1マスに進める。
     *
     * @param move 指し手
     */
    private static void validateGyokuMove(SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        if (Math.abs(rowDiff) > 1 || Math.abs(colDiff) > 1) {
            throw new IllegalArgumentException("王は周囲1マスにだけ進めます");
        }
    }

    /**
     * 飛車の移動ルールを検証する。
     *
     * 飛車は縦横に何マスでも進める。
     * ただし、途中に駒がある場合は進めない。
     *
     * @param board 盤面
     * @param move 指し手
     */
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

    /**
     * 角の移動ルールを検証する。
     *
     * 角は斜めに何マスでも進める。
     * ただし、途中に駒がある場合は進めない。
     *
     * @param board 盤面
     * @param move 指し手
     */
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

    /**
     * 龍の移動ルールを検証する。
     *
     * 龍は飛車の動きに加えて、斜め1マスに進める。
     *
     * @param board 盤面
     * @param move 指し手
     */
    private static void validatePromotedHishaMove(Board board, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        boolean rookMove = rowDiff == 0 || colDiff == 0;
        boolean diagonalOneStep = Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1;

        if (!rookMove && !diagonalOneStep) {
            throw new IllegalArgumentException("龍は縦横または斜め1マスに進めます");
        }

        if (rookMove && !isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    /**
     * 馬の移動ルールを検証する。
     *
     * 馬は角の動きに加えて、縦横1マスに進める。
     *
     * @param board 盤面
     * @param move 指し手
     */
    private static void validatePromotedKakuMove(Board board, SfenMove move) {
        int rowDiff = move.getTo().getRow() - move.getFrom().getRow();
        int colDiff = move.getTo().getCol() - move.getFrom().getCol();

        boolean bishopMove = Math.abs(rowDiff) == Math.abs(colDiff);
        boolean straightOneStep = Math.abs(rowDiff) + Math.abs(colDiff) == 1;

        if (!bishopMove && !straightOneStep) {
            throw new IllegalArgumentException("馬は斜めまたは縦横1マスに進めます");
        }

        if (bishopMove && !isPathClear(board, move.getFrom(), move.getTo())) {
            throw new IllegalArgumentException("移動経路に駒があります");
        }
    }

    /**
     * プレイヤーごとの前方向を取得する。
     *
     * 先手は上方向、後手は下方向に進む。
     *
     * @param owner 駒の所有者
     * @return 先手は -1、後手は 1
     */
    private static int getForwardDirection(PlayerType owner) {
        return owner == PlayerType.SENTE ? -1 : 1;
    }

    /**
     * 飛車・角・香車などの移動経路に駒がないかを判定する。
     *
     * 移動元と移動先は判定対象に含めず、その間のマスだけを確認する。
     *
     * @param board 盤面
     * @param from 移動元
     * @param to 移動先
     * @return 経路上に駒がなければ true
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