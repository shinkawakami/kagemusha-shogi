package com.kagemusha.backend.domain;

import java.util.EnumMap;
import java.util.Map;

public class CapturedPieces {

    private final Map<PlayerType, Map<PieceType, Integer>> pieces = new EnumMap<>(PlayerType.class);

    public CapturedPieces() {
        pieces.put(PlayerType.SENTE, new EnumMap<>(PieceType.class));
        pieces.put(PlayerType.GOTE, new EnumMap<>(PieceType.class));
    }

    /**
     * 持ち駒を追加する
     */
    public void add(PlayerType owner, PieceType pieceType) {
        Map<PieceType, Integer> ownerPieces = pieces.get(owner);

        int currentCount = ownerPieces.getOrDefault(pieceType, 0);
        ownerPieces.put(pieceType, currentCount + 1);
    }

    /**
     * 持ち駒を減らす
     */
    public void remove(PlayerType owner, PieceType pieceType) {
        Map<PieceType, Integer> ownerPieces = pieces.get(owner);

        int currentCount = ownerPieces.getOrDefault(pieceType, 0);

        if (currentCount <= 0) {
            throw new IllegalArgumentException("指定された持ち駒を持っていません");
        }

        if (currentCount == 1) {
            ownerPieces.remove(pieceType);
        } else {
            ownerPieces.put(pieceType, currentCount - 1);
        }
    }

    /**
     * 指定した持ち駒の枚数を取得する
     */
    public int count(PlayerType owner, PieceType pieceType) {
        return pieces.get(owner).getOrDefault(pieceType, 0);
    }

    /**
     * 指定プレイヤーの持ち駒一覧を取得する
     */
    public Map<PieceType, Integer> getPieces(PlayerType owner) {
        return pieces.get(owner);
    }
}