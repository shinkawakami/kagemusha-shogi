package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.PieceType;

/**
 * 駒種（{@link PieceType}）と SFEN 駒文字の相互変換。
 *
 * <p>SFEN では先手の駒を大文字、後手の駒を小文字で表すため、
 * ここでは大文字の基本文字のみを扱う（大小の区別は呼び出し側の責務）。
 * SFEN 表現をドメイン型（{@code PieceType}）から切り離すためのヘルパー。
 */
final class SfenPieceSymbol {

    private SfenPieceSymbol() {
    }

    /**
     * 駒種を SFEN の基本駒文字（大文字）に変換する。
     */
    static String of(PieceType type) {
        return switch (type) {
            case GYOKU -> "K";
            case HISHA -> "R";
            case KAKU -> "B";
            case KIN -> "G";
            case GIN -> "S";
            case KEIMA -> "N";
            case KYO -> "L";
            case FU -> "P";
        };
    }

    /**
     * SFEN の駒文字から駒種を取得する。
     *
     * <p>先手・後手で大文字・小文字が異なるため、大小を区別せず判定する。
     * 例: {@code P} または {@code p} は {@link PieceType#FU}。
     *
     * @throws IllegalArgumentException 対応する駒種が存在しない場合
     */
    static PieceType fromSymbol(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "K" -> PieceType.GYOKU;
            case "R" -> PieceType.HISHA;
            case "B" -> PieceType.KAKU;
            case "G" -> PieceType.KIN;
            case "S" -> PieceType.GIN;
            case "N" -> PieceType.KEIMA;
            case "L" -> PieceType.KYO;
            case "P" -> PieceType.FU;
            default -> throw new IllegalArgumentException("不正なSFEN駒文字です: " + symbol);
        };
    }
}
