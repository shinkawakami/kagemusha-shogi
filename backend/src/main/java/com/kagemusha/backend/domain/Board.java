package com.kagemusha.backend.domain;

/**
 * 将棋の盤面を表すクラス。
 *
 * 9×9 のマスを {@link Piece} の二次元配列で管理する。
 * 配列上では、駒が存在しないマスは {@code null} として扱う。
 */
public class Board {

    /**
     * 盤面上のマス。
     *
     * 第一添字が行、第二添字が列を表す。
     * {@link Position#toArrayRow()} と {@link Position#toArrayCol()} によって
     * 将棋の座標を配列の添字に変換してアクセスする。
     */
    private final Piece[][] squares;

    /**
     * 空の 9×9 盤面を生成する。
     */
    public Board() {
        this.squares = new Piece[9][9];
    }

    /**
     * 指定した位置にある駒を取得する。
     *
     * @param position 取得対象の位置
     * @return 指定位置にある駒。駒がない場合は {@code null}
     */
    public Piece getPiece(Position position) {
        return squares[position.toArrayRow()][position.toArrayCol()];
    }

    /**
     * 指定した位置に駒を配置する。
     *
     * すでに駒が存在する位置に配置した場合、その駒は上書きされる。
     *
     * @param position 配置する位置
     * @param piece 配置する駒
     */
    public void setPiece(Position position, Piece piece) {
        squares[position.toArrayRow()][position.toArrayCol()] = piece;
    }

    /**
     * 指定した位置から駒を取り除く。
     *
     * @param position 駒を取り除く位置
     */
    public void removePiece(Position position) {
        squares[position.toArrayRow()][position.toArrayCol()] = null;
    }
}