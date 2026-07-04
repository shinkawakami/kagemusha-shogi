package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.Position;

public class SfenMoveParser {

    private SfenMoveParser() {
    }

    public static SfenMove parse(String move) {
        if (move == null || move.isBlank()) {
            throw new IllegalArgumentException("指し手が空です");
        }

        if (move.length() != 4 && move.length() != 5) {
            throw new IllegalArgumentException("指し手の形式が不正です: " + move);
        }

        Position from = parsePosition(move.substring(0, 2));
        Position to = parsePosition(move.substring(2, 4));
        boolean promote = move.length() == 5 && move.charAt(4) == '+';

        return new SfenMove(from, to, promote);
    }

    private static Position parsePosition(String text) {
        if (text.length() != 2) {
            throw new IllegalArgumentException("座標の形式が不正です: " + text);
        }

        char fileChar = text.charAt(0);
        char rankChar = text.charAt(1);

        if (fileChar < '1' || fileChar > '9') {
            throw new IllegalArgumentException("筋の指定が不正です: " + text);
        }

        if (rankChar < 'a' || rankChar > 'i') {
            throw new IllegalArgumentException("段の指定が不正です: " + text);
        }

        int file = Character.getNumericValue(fileChar);
        int row = rankChar - 'a' + 1;

        // SFEN/USIでは右上が1a、左上が9a。
        // Boardの配列は左から右へ col=1〜9 として扱うため、
        // 1筋は配列上の右端、9筋は左端になるように反転する。
        int col = 10 - file;

        return new Position(row, col);
    }
}