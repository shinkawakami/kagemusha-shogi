package com.kagemusha.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * スケジュール実行（{@link org.springframework.scheduling.annotation.Scheduled @Scheduled}）を有効化する。
 *
 * <p>放置対局の定期削除などのバックグラウンドジョブで利用する。
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
