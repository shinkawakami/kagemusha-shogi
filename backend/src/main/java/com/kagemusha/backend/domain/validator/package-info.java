/**
 * 指し手の合法性を検証するドメインサービス群。
 *
 * <p>いずれも状態を持たない静的ユーティリティで、将棋の指し手ルール
 * （移動範囲・持ち駒打ち・成り）を表現する。{@code Game} アグリゲートが
 * 着手処理（{@code Game.move}）の中から呼び出す。
 *
 * <ul>
 *   <li>{@link com.kagemusha.backend.domain.validator.MoveValidator} — 通常の移動の総合検証</li>
 *   <li>{@link com.kagemusha.backend.domain.validator.CommonMoveValidator} — 移動に共通する前提条件</li>
 *   <li>{@link com.kagemusha.backend.domain.validator.DropMoveValidator} — 持ち駒打ちの検証</li>
 *   <li>{@link com.kagemusha.backend.domain.validator.PromotionValidator} — 成りの可否の検証</li>
 * </ul>
 */
package com.kagemusha.backend.domain.validator;
