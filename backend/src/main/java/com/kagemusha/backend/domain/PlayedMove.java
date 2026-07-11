package com.kagemusha.backend.domain;

/**
 * 実際に指された1手の記録（棋譜の1エントリ）。
 *
 * <p>永続化では追記オンリーの {@code game_moves} テーブルに対応する。
 * これらの列（ply / move_sfen / played_by）を保持する。
 *
 * @param ply      手数（1 始まりの通し番号）
 * @param moveSfen SFEN/USI 形式の指し手文字列（例 "7g7f"、"P*5e"）
 * @param playedBy 指したプレイヤー
 */
public record PlayedMove(int ply, String moveSfen, PlayerType playedBy) {
}
