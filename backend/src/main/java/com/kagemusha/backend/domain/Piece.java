package com.kagemusha.backend.domain;

/**
 * 将棋の駒を表すクラス。
 *
 * 駒の種類、所有者、成り状態を持つ。
 * このクラスは不変オブジェクトとして扱い、生成後に状態は変更しない。
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
     * 駒をSFEN形式の文字列に変換する。
     *
     * 先手の駒は大文字、後手の駒は小文字で表す。
     * 成り駒の場合は、駒文字の前に {@code +} を付ける。
     *
     * 例：
     * 先手の歩は {@code P}、
     * 後手の歩は {@code p}、
     * 先手の成歩は {@code +P}、
     * 後手の成歩は {@code +p}。
     *
     * @return SFEN形式の駒文字列
     */
    public String toSfenSymbol() {
        String symbol = type.getSfenSymbol();

        if (owner == PlayerType.GOTE) {
            symbol = symbol.toLowerCase();
        }

        return promoted ? "+" + symbol : symbol;
    }
}