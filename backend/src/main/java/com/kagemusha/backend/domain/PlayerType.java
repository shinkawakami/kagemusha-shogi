package com.kagemusha.backend.domain;

/**
 * プレイヤーの種類を表す列挙型。
 *
 * 将棋における先手・後手を表す。
 * SFENでは、先手を {@code b}、後手を {@code w} として表現する。
 */
public enum PlayerType {
    SENTE,
    GOTE;

    /**
     * 相手プレイヤーを取得する。
     *
     * 現在のプレイヤーが先手の場合は後手、
     * 後手の場合は先手を返す。
     * ターン切り替え処理で使用する。
     *
     * @return 相手プレイヤー
     */
    public PlayerType opposite() {
        return this == SENTE ? GOTE : SENTE;
    }

    /**
     * SFENの手番文字からプレイヤー種別を取得する。
     *
     * {@code b} は先手、{@code w} は後手として扱う。
     *
     * @param turn SFENの手番文字
     * @return 対応するプレイヤー種別
     * @throws IllegalArgumentException {@code b} または {@code w} 以外が渡された場合
     */
    public static PlayerType fromSfenTurn(String turn) {
        if ("b".equals(turn)) {
            return SENTE;
        }

        if ("w".equals(turn)) {
            return GOTE;
        }

        throw new IllegalArgumentException("不正な手番です: " + turn);
    }

    /**
     * プレイヤー種別をSFENの手番文字に変換する。
     *
     * 先手は {@code b}、後手は {@code w} を返す。
     *
     * @return SFENの手番文字
     */
    public String toSfenTurn() {
        return this == SENTE ? "b" : "w";
    }
}