package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.PieceType;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;

public class SfenMove {

    private final Position from;
    private final Position to;
    private final boolean promote;

    private final PieceType dropPieceType;
    private final PlayerType dropOwner;

    private SfenMove(
            Position from,
            Position to,
            boolean promote,
            PieceType dropPieceType,
            PlayerType dropOwner
    ) {
        this.from = from;
        this.to = to;
        this.promote = promote;
        this.dropPieceType = dropPieceType;
        this.dropOwner = dropOwner;
    }

    public static SfenMove normalMove(Position from, Position to, boolean promote) {
        return new SfenMove(from, to, promote, null, null);
    }

    public static SfenMove dropMove(PieceType dropPieceType, PlayerType dropOwner, Position to) {
        return new SfenMove(null, to, false, dropPieceType, dropOwner);
    }

    public boolean isDrop() {
        return dropPieceType != null;
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

    public PieceType getDropPieceType() {
        return dropPieceType;
    }

    public PlayerType getDropOwner() {
        return dropOwner;
    }
}