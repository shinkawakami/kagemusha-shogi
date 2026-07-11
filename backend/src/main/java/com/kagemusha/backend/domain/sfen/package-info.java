/**
 * SFEN/USI 形式とドメインモデルの相互変換を担うパッケージ。
 *
 * <p><b>方針:</b> SFEN は外部（USI プロトコル）由来の表現フォーマットであり、
 * その変換ロジックはすべてこのパッケージに集約する。
 * {@code domain} 直下のドメイン型（{@code Piece}・{@code PieceType}・
 * {@code PlayerType}・{@code Position} など）は SFEN 形式を一切知らない。
 *
 * <p>盤面・持ち駒・局面全体といった複数トークンから成る合成構造の変換は
 * {@link com.kagemusha.backend.domain.sfen.SfenConverter} が担い、
 * 駒文字・座標などの個別トークンの変換は
 * {@link com.kagemusha.backend.domain.sfen.SfenPieceSymbol} /
 * {@link com.kagemusha.backend.domain.sfen.SfenPositionConverter} が担う。
 */
package com.kagemusha.backend.domain.sfen;
