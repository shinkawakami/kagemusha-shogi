package com.kagemusha.backend.domain;

public enum PieceType {

    GYOKU("王"),
    HISHA("飛"),
    KAKU("角"),
    KIN("金"),
    GIN("銀"),
    KEIMA("桂"),
    KYO("香"),
    FU("歩");

    private final String label;

    PieceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}