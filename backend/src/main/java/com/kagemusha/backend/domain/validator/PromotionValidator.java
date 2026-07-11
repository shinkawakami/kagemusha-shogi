package com.kagemusha.backend.domain.validator;

import com.kagemusha.backend.domain.Piece;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenMove;

public class PromotionValidator {

    private PromotionValidator() {
    }

    /**
     * 成り指定の妥当性を検証する。
     *
     * <p>成り指定がある場合は、成れる駒か・成れる位置か・すでに成っていないかを確認する。
     * 成り指定がない場合は、成らないことで行き所のない駒にならないか（強制成り）を確認する。
     *
     * @param movingPiece 移動する駒
     * @param move 指し手
     */
    public static void validate(Piece movingPiece, SfenMove move) {
        if (move.isPromote()) {
            validatePromotion(movingPiece, move);
        } else {
            validateNotForcedToPromote(movingPiece, move);
        }
    }

    /**
     * 成る場合の妥当性を検証する。
     */
    private static void validatePromotion(Piece movingPiece, SfenMove move) {
        if (movingPiece.isPromoted()) {
            throw new IllegalArgumentException("すでに成っている駒は再度成れません");
        }

        if (!movingPiece.getType().canPromote()) {
            throw new IllegalArgumentException("この駒は成れません: " + movingPiece.getType());
        }

        if (!isPromotionZone(movingPiece.getOwner(), move.getFrom())
                && !isPromotionZone(movingPiece.getOwner(), move.getTo())) {
            throw new IllegalArgumentException("敵陣に入る、敵陣から出る、または敵陣内で移動する場合のみ成れます");
        }
    }

    /**
     * 成らない場合に、行き所のない駒にならないかを検証する。
     *
     * <p>不成の歩・香が最終段、桂が最終2段へ進む手は、強制成りのため不成では指せない。
     * すでに成っている駒（金と同じ動き）は行き所を失わないため対象外。
     */
    private static void validateNotForcedToPromote(Piece movingPiece, SfenMove move) {
        if (movingPiece.isPromoted()) {
            return;
        }

        if (StuckPieceRule.hasNoFuture(movingPiece.getOwner(), movingPiece.getType(), move.getTo().getRow())) {
            throw new IllegalArgumentException("行き所のない駒になるため、成らずにこのマスへは進めません");
        }
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
