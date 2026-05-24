package com.kagemusha.backend.domain;

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

        // SFENの筋は「右から1〜9」
        // そのため Position の col も「右から1〜9」として扱う
        int col = file;

        return new Position(row, col);
    }
}