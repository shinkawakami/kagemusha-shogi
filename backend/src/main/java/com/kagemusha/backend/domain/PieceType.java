package com.kagemusha.backend.domain;

/**
 * 将棋の駒の種類を表す列挙型。
 *
 * 各駒種は、SFENで使用する駒文字と、
 * 画面表示などで使用する日本語名を持つ。
 */
public enum PieceType {
    GYOKU("K", "王"),
    HISHA("R", "飛"),
    KAKU("B", "角"),
    KIN("G", "金"),
    GIN("S", "銀"),
    KEIMA("N", "桂"),
    KYO("L", "香"),
    FU("P", "歩");

    /**
     * SFENで使用する駒文字。
     *
     * 先手の場合は大文字、後手の場合は小文字で表すため、
     * {@link PieceType} では大文字の基本文字のみを保持する。
     */
    private final String sfenSymbol;

    /**
     * 画面表示などで使用する日本語の駒名。
     */
    private final String displayName;

    /**
     * 駒種を生成する。
     *
     * @param sfenSymbol SFENで使用する駒文字
     * @param displayName 表示用の駒名
     */
    PieceType(String sfenSymbol, String displayName) {
        this.sfenSymbol = sfenSymbol;
        this.displayName = displayName;
    }

    /**
     * SFENで使用する駒文字を取得する。
     *
     * @return SFEN駒文字
     */
    public String getSfenSymbol() {
        return sfenSymbol;
    }

    /**
     * 表示用の駒名を取得する。
     *
     * @return 日本語の駒名
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * SFENの駒文字から対応する駒種を取得する。
     *
     * SFENでは先手の駒を大文字、後手の駒を小文字で表すため、
     * ここでは大文字・小文字を区別せずに判定する。
     *
     * 例：
     * {@code P} または {@code p} は {@link #FU}、
     * {@code R} または {@code r} は {@link #HISHA} に変換される。
     *
     * @param symbol SFENの駒文字
     * @return 対応する駒種
     * @throws IllegalArgumentException 対応する駒種が存在しない場合
     */
    public static PieceType fromSfenSymbol(String symbol) {
        String upperSymbol = symbol.toUpperCase();

        for (PieceType type : values()) {
            if (type.sfenSymbol.equals(upperSymbol)) {
                return type;
            }
        }

        throw new IllegalArgumentException("不正なSFEN駒文字です: " + symbol);
    }
}