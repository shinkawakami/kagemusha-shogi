package com.kagemusha.backend.domain.validator;

import com.kagemusha.backend.domain.Piece;
import com.kagemusha.backend.domain.PieceType;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenMove;

public class PromotionValidator {

    private PromotionValidator() {
    }

    /**
     * 成り指定が正しいか検証する。
     *
     * 成り指定がない場合は何もしない。
     * 成り指定がある場合、成れる駒か、成れる位置か、すでに成っていないかを確認する。
     *
     * @param movingPiece 移動する駒
     * @param move 指し手
     */
    public static void validate(Piece movingPiece, SfenMove move) {
        if (!move.isPromote()) {
            return;
        }

        if (movingPiece.isPromoted()) {
            throw new IllegalArgumentException("すでに成っている駒は再度成れません");
        }

        if (!canPromote(movingPiece.getType())) {
            throw new IllegalArgumentException("この駒は成れません: " + movingPiece.getType());
        }

        if (!isPromotionZone(movingPiece.getOwner(), move.getFrom())
                && !isPromotionZone(movingPiece.getOwner(), move.getTo())) {
            throw new IllegalArgumentException("敵陣に入る、敵陣から出る、または敵陣内で移動する場合のみ成れます");
        }
    }

    /**
     * 成れる駒種かどうかを判定する。
     *
     * 王と金は成れない。
     *
     * @param type 駒種
     * @return 成れる場合は true
     */
    private static boolean canPromote(PieceType type) {
        return switch (type) {
            case FU, KYO, KEIMA, GIN, HISHA, KAKU -> true;
            case KIN, GYOKU -> false;
        };
    }

    /**
     * 指定位置がそのプレイヤーから見て敵陣かどうかを判定する。
     *
     * 先手の敵陣は1〜3段目。
     * 後手の敵陣は7〜9段目。
     *
     * @param owner 駒の所有者
     * @param position 判定する位置
     * @return 敵陣の場合は true
     */
    private static boolean isPromotionZone(PlayerType owner, Position position) {
        int row = position.getRow();

        if (owner == PlayerType.SENTE) {
            return row >= 1 && row <= 3;
        }

        return row >= 7 && row <= 9;
    }
}