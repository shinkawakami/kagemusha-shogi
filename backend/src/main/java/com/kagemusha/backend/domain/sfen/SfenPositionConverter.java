package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.Position;

public class SfenPositionConverter {

    private SfenPositionConverter() {
    }

    public static Position toPosition(String text) {
        if (text == null || text.length() != 2) {
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

        // SFEN/USI: 右上が1a、左上が9a
        // Board: 左から右へ col=1〜9
        int col = 10 - file;

        return new Position(row, col);
    }

    /**
     * Position を SFEN/USI形式の座標文字列に変換する。
     *
     * toPosition の逆変換。
     *
     * 例:
     * (row=7, col=3) → "7g"
     */
    public static String toSfen(Position position) {
        int file = 10 - position.getCol();
        char rankChar = (char) ('a' + position.getRow() - 1);

        return "" + file + rankChar;
    }
}