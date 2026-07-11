package com.kagemusha.backend.domain.validator;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.PieceType;
import com.kagemusha.backend.domain.PlayerType;

/**
 * 「行き所のない駒」の判定。
 *
 * <p>不成の歩・香・桂は、それ以上前進できない段に置くことができない。
 * 持ち駒打ち（{@link DropMoveValidator}）と移動時の強制成り
 * （{@link PromotionValidator}）で共有する。
 */
final class StuckPieceRule {

    private StuckPieceRule() {
    }

    /**
     * 不成のまま指定段に置くと二度と前進できなくなる駒かどうかを判定する。
     *
     * <p>先手は段が小さい方へ、後手は段が大きい方へ進む。
     * <ul>
     *   <li>歩・香: 最終段（先手1段目 / 後手9段目）</li>
     *   <li>桂: 最終2段（先手1〜2段目 / 後手8〜9段目）</li>
     * </ul>
     *
     * @param owner 駒の所有者
     * @param type 駒種
     * @param row 置こうとする段（1〜9）
     * @return 行き所がなくなる場合は true
     */
    static boolean hasNoFuture(PlayerType owner, PieceType type, int row) {
        return switch (type) {
            case FU, KYO -> owner == PlayerType.SENTE ? row == 1 : row == Board.SIZE;
            case KEIMA -> owner == PlayerType.SENTE ? row <= 2 : row >= Board.SIZE - 1;
            default -> false;
        };
    }
}
