package com.kagemusha.backend.domain;

import java.util.Objects;

/**
 * 将棋の駒を表す値オブジェクト。
 *
 * 駒の種類、所有者、成り状態を持つ。
 * 不変オブジェクトであり、種類・所有者・成り状態がすべて等しい駒は等価として扱う。
 */
public class Piece {

    /**
     * 駒の種類。
     *
     * 歩、香、桂、銀、金、王、飛、角などを表す。
     */
    private final PieceType type;

    /**
     * 駒の所有者。
     *
     * 先手または後手を表す。
     */
    private final PlayerType owner;

    /**
     * 成り駒かどうか。
     *
     * {@code true} の場合は成り駒、
     * {@code false} の場合は通常の駒を表す。
     */
    private final boolean promoted;

    /**
     * 成っていない駒を生成する。
     *
     * @param type 駒の種類
     * @param owner 駒の所有者
     */
    public Piece(PieceType type, PlayerType owner) {
        this(type, owner, false);
    }

    /**
     * 駒を生成する。
     *
     * @param type 駒の種類
     * @param owner 駒の所有者
     * @param promoted 成り駒かどうか
     */
    public Piece(PieceType type, PlayerType owner, boolean promoted) {
        this.type = type;
        this.owner = owner;
        this.promoted = promoted;
    }

    /**
     * 駒の種類を取得する。
     *
     * @return 駒の種類
     */
    public PieceType getType() {
        return type;
    }

    /**
     * 駒の所有者を取得する。
     *
     * @return 駒の所有者
     */
    public PlayerType getOwner() {
        return owner;
    }

    /**
     * 成り駒かどうかを判定する。
     *
     * @return 成り駒の場合は {@code true}、通常の駒の場合は {@code false}
     */
    public boolean isPromoted() {
        return promoted;
    }

    /**
     * 種類・所有者・成り状態がすべて等しい場合に等価とみなす。
     *
     * @param o 比較対象
     * @return 等価な駒の場合は true
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece piece)) return false;
        return promoted == piece.promoted && type == piece.type && owner == piece.owner;
    }

    /**
     * 駒のハッシュ値を返す。
     *
     * equals と同じく、種類・所有者・成り状態をもとに生成する。
     *
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, owner, promoted);
    }
}
