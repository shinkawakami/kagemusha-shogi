package com.kagemusha.backend.port.in;

/**
 * 放置対局の掃除ユースケース（ヘキサゴナルアーキテクチャの inbound port）。
 *
 * <p>スケジューラ（inbound adapter）はこのインターフェースにのみ依存し、
 * application 層の具象には直接依存しない。
 *
 * <p>「放置」の判定基準（終局していない かつ 一定期間更新がない）と保持期間（TTL）は
 * application 層の関心事であり、このポートには漏らさない。
 */
public interface CleanUpAbandonedGamesUseCase {

    /**
     * 放置された対局を削除する。
     *
     * @return 削除した対局数
     */
    int cleanUp();
}
