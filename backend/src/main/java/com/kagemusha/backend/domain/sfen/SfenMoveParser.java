package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.PieceType;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;

public class SfenMoveParser {

    private SfenMoveParser() {
    }

    public static SfenMove parse(String move) {
        if (move == null || move.isBlank()) {
            throw new IllegalArgumentException("指し手が空です");
        }

        if (isDropMove(move)) {
            return parseDropMove(move);
        }

        if (move.length() != 4 && move.length() != 5) {
            throw new IllegalArgumentException("指し手の形式が不正です: " + move);
        }

        Position from = parsePosition(move.substring(0, 2));
        Position to = parsePosition(move.substring(2, 4));
        boolean promote = move.length() == 5 && move.charAt(4) == '+';

        return SfenMove.normalMove(from, to, promote);
    }

    /**
     * 持ち駒打ちかどうかを判定する。
     *
     * 例:
     * P2d
     * p2d
     * R5e
     */
    private static boolean isDropMove(String move) {
        if (move.length() != 3) {
            return false;
        }

        char pieceChar = move.charAt(0);
        char fileChar = move.charAt(1);
        char rankChar = move.charAt(2);

        boolean piecePart = Character.isLetter(pieceChar);
        boolean filePart = fileChar >= '1' && fileChar <= '9';
        boolean rankPart = rankChar >= 'a' && rankChar <= 'i';

        return piecePart && filePart && rankPart;
    }

    /**
     * 持ち駒打ちを解析する。
     *
     * 例:
     * P2d
     * p2d
     */
    private static SfenMove parseDropMove(String move) {
        char pieceChar = move.charAt(0);

        PieceType pieceType = SfenPieceSymbol.fromSymbol(String.valueOf(pieceChar));

        if (pieceType == PieceType.GYOKU) {
            throw new IllegalArgumentException("王は持ち駒として打てません: " + move);
        }

        PlayerType owner = Character.isUpperCase(pieceChar)
                ? PlayerType.SENTE
                : PlayerType.GOTE;

        Position to = parsePosition(move.substring(1, 3));

        return SfenMove.dropMove(pieceType, owner, to);
    }

    private static Position parsePosition(String text) {
        return SfenPositionConverter.toPosition(text);
    }
}