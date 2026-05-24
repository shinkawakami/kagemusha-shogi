package com.kagemusha.backend.domain;

public enum PieceType {
    GYOKU("K", "王"),
    HISHA("R", "飛"),
    KAKU("B", "角"),
    KIN("G", "金"),
    GIN("S", "銀"),
    KEIMA("N", "桂"),
    KYO("L", "香"),
    FU("P", "歩");

    private final String sfenSymbol;
    private final String displayName;

    PieceType(String sfenSymbol, String displayName) {
        this.sfenSymbol = sfenSymbol;
        this.displayName = displayName;
    }

    public String getSfenSymbol() {
        return sfenSymbol;
    }

    public String getDisplayName() {
        return displayName;
    }

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