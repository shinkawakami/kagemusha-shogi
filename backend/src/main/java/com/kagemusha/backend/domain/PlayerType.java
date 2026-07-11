package com.kagemusha.backend.domain;

/**
 * プレイヤーの種類を表す列挙型。
 *
 * <p>将棋における先手・後手を表す。SFEN の手番文字（{@code b}/{@code w}）との
 * 相互変換は {@code domain.sfen} パッケージに集約する。
 */
public enum PlayerType {
    SENTE,
    GOTE;

    /**
     * 相手プレイヤーを取得する。
     *
     * 現在のプレイヤーが先手の場合は後手、
     * 後手の場合は先手を返す。
     * ターン切り替え処理で使用する。
     *
     * @return 相手プレイヤー
     */
    public PlayerType opponent() {
        return this == SENTE ? GOTE : SENTE;
    }
}
