package com.kagemusha.backend.domain;

public enum PlayerType {
    SENTE,
    GOTE;

    public PlayerType opposite() {
        return this == SENTE ? GOTE : SENTE;
    }

    public static PlayerType fromSfenTurn(String turn) {
        if ("b".equals(turn)) {
            return SENTE;
        }

        if ("w".equals(turn)) {
            return GOTE;
        }

        throw new IllegalArgumentException("不正な手番です: " + turn);
    }

    public String toSfenTurn() {
        return this == SENTE ? "b" : "w";
    }
}