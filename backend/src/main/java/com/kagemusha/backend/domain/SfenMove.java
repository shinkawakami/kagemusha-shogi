package com.kagemusha.backend.domain;

public class SfenMove {

    private final Position from;
    private final Position to;
    private final boolean promote;

    public SfenMove(Position from, Position to, boolean promote) {
        this.from = from;
        this.to = to;
        this.promote = promote;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public boolean isPromote() {
        return promote;
    }
}