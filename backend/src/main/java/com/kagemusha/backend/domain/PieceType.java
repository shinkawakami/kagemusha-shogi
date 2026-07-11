package com.kagemusha.backend.domain;

/**
 * 将棋の駒の種類を表す列挙型。
 *
 * <p>ドメインの概念のみを表し、SFEN などの外部表現は持たない。
 * SFEN 文字との相互変換は {@code domain.sfen} パッケージに集約する。
 */
public enum PieceType {
    GYOKU,
    HISHA,
    KAKU,
    KIN,
    GIN,
    KEIMA,
    KYO,
    FU;

    /**
     * この駒が成れるかどうかを返す。
     *
     * <p>王と金は成れない。それ以外の駒は成れる。
     *
     * @return 成れる場合は true
     */
    public boolean canPromote() {
        return switch (this) {
            case FU, KYO, KEIMA, GIN, HISHA, KAKU -> true;
            case KIN, GYOKU -> false;
        };
    }
}
