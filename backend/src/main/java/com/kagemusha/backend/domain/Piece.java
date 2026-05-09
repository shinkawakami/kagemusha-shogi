package com.kagemusha.backend.domain;

public class Piece {

    private PieceType type;
    private PlayerType owner;
    private Position position;
    private boolean promoted;

    public Piece(PieceType type, PlayerType owner, Position position) {
        this.type = type;
        this.owner = owner;
        this.position = position;
        this.promoted = false;
    }

    public Piece(PieceType type, PlayerType owner, Position position, boolean promoted) {
        this.type = type;
        this.owner = owner;
        this.position = position;
        this.promoted = promoted;
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public PlayerType getOwner() {
        return owner;
    }

    public void setOwner(PlayerType owner) {
        this.owner = owner;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }
}
