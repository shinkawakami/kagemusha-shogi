package com.kagemusha.backend.domain;

import java.util.Objects;

/**
 * 将棋盤上の位置を表すクラス。
 *
 * row と col はどちらも 1〜9 の値で管理する。
 * 配列の添字として使う場合は、toArrayRow() と toArrayCol() で
 * 0〜8 の値に変換する。
 */
public class Position {

    /**
     * 行番号。
     *
     * 1〜9 の値を持つ。
     */
    private final int row;

    /**
     * 列番号。
     *
     * 1〜9 の値を持つ。
     */
    private final int col;

    /**
     * 位置を生成する。
     *
     * 盤外の座標が指定された場合は例外を投げる。
     *
     * @param row 行番号
     * @param col 列番号
     * @throws IllegalArgumentException row または col が 1〜9 の範囲外の場合
     */
    public Position(int row, int col) {
        if (!isInsideBoard(row, col)) {
            throw new IllegalArgumentException("盤外の座標です: row=" + row + ", col=" + col);
        }

        this.row = row;
        this.col = col;
    }

    /**
     * 指定した行・列が盤内かどうかを判定する。
     *
     * @param row 行番号
     * @param col 列番号
     * @return 盤内の場合は true、盤外の場合は false
     */
    public static boolean isInsideBoard(int row, int col) {
        return row >= 1 && row <= 9 && col >= 1 && col <= 9;
    }

    /**
     * この位置が盤内かどうかを判定する。
     *
     * コンストラクタで盤外の値を拒否しているため、
     * 通常は常に true になる。
     *
     * @return 盤内の場合は true
     */
    public boolean isInsideBoard() {
        return isInsideBoard(row, col);
    }

    /**
     * 行番号を取得する。
     *
     * @return 行番号
     */
    public int getRow() {
        return row;
    }

    /**
     * 列番号を取得する。
     *
     * @return 列番号
     */
    public int getCol() {
        return col;
    }

    /**
     * 配列アクセス用の行インデックスに変換する。
     *
     * Position では 1〜9 で管理しているため、
     * 配列用に 0〜8 の値へ変換する。
     *
     * @return 配列用の行インデックス
     */
    public int toArrayRow() {
        return row - 1;
    }

    /**
     * 配列アクセス用の列インデックスに変換する。
     *
     * Position では 1〜9 で管理しているため、
     * 配列用に 0〜8 の値へ変換する。
     *
     * @return 配列用の列インデックス
     */
    public int toArrayCol() {
        return col - 1;
    }

    /**
     * 配列の添字から Position を生成する。
     *
     * 配列では 0〜8 のインデックスを使うため、
     * Position 用に 1〜9 の座標へ変換する。
     *
     * @param rowIndex 配列用の行インデックス
     * @param colIndex 配列用の列インデックス
     * @return 変換後の Position
     */
    public static Position fromArrayIndex(int rowIndex, int colIndex) {
        return new Position(rowIndex + 1, colIndex + 1);
    }

    /**
     * 他の Position と同じ座標かどうかを判定する。
     *
     * row と col がどちらも同じ場合、同じ位置として扱う。
     *
     * @param o 比較対象
     * @return 同じ位置の場合は true、異なる場合は false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position position)) return false;
        return row == position.row && col == position.col;
    }

    /**
     * Position のハッシュ値を返す。
     *
     * equals と同じく、row と col をもとに生成する。
     *
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}