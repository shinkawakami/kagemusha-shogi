package com.kagemusha.backend.domain;

public class Piece {

    private final PieceType type;
    private final PlayerType owner;
    private final boolean promoted;

    public Piece(PieceType type, PlayerType owner) {
        this(type, owner, false);
    }

    public Piece(PieceType type, PlayerType owner, boolean promoted) {
        this.type = type;
        this.owner = owner;
        this.promoted = promoted;
    }

    public PieceType getType() {
        return type;
    }

    public PlayerType getOwner() {
        return owner;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public String toSfenSymbol() {
        String symbol = type.getSfenSymbol();

        if (owner == PlayerType.GOTE) {
            symbol = symbol.toLowerCase();
        }

        return promoted ? "+" + symbol : symbol;
    }
}