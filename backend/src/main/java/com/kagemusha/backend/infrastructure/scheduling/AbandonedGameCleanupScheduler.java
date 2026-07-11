package com.kagemusha.backend.infrastructure.scheduling;

import com.kagemusha.backend.port.in.CleanUpAbandonedGamesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 放置対局の掃除を定期実行する inbound adapter（駆動アダプタ）。
 *
 * <p>スケジュールは cron（{@code app.game.cleanup-cron}、既定は毎正時）で駆動し、
 * 実際の掃除ロジックは {@link CleanUpAbandonedGamesUseCase} に委ねる。
 * スケジューリング（技術的関心事）を application 層に持ち込まないためにこの層に置く。
 */
@Component
public class AbandonedGameCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(AbandonedGameCleanupScheduler.class);

    private final CleanUpAbandonedGamesUseCase cleanUpAbandonedGames;

    public AbandonedGameCleanupScheduler(CleanUpAbandonedGamesUseCase cleanUpAbandonedGames) {
        this.cleanUpAbandonedGames = cleanUpAbandonedGames;
    }

    @Scheduled(cron = "${app.game.cleanup-cron:0 0 * * * *}")
    public void cleanUp() {
        int deleted = cleanUpAbandonedGames.cleanUp();

        if (deleted > 0) {
            log.info("放置対局を {} 件削除しました", deleted);
        }
    }
}
