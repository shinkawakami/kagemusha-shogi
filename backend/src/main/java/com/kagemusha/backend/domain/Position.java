package com.kagemusha.backend.domain;

import java.util.Objects;

public class Position {

    private final int row;
    private final int col;

    public Position(int row, int col) {
        if (row < 1 || row > 9 || col < 1 || col > 9) {
            throw new IllegalArgumentException("盤外の座標です: row=" + row + ", col=" + col);
        }

        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int toArrayRow() {
        return row - 1;
    }

    public int toArrayCol() {
        return col - 1;
    }

    public static Position fromArrayIndex(int rowIndex, int colIndex) {
        return new Position(rowIndex + 1, colIndex + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position position)) return false;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}