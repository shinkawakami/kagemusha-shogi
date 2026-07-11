package com.kagemusha.backend.application;

import com.kagemusha.backend.port.in.CleanUpAbandonedGamesUseCase;
import com.kagemusha.backend.port.out.GameRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * 放置対局の掃除ユースケースの application 層実装。
 *
 * <p>終局していない対局のうち、一定期間（{@code app.game.abandoned-ttl}）更新がないものを
 * 放置とみなして削除する。相手待ちのまま参加されない対局や、途中で中断された対局を掃除する。
 * 終局済みの対局は棋譜・戦績として保持するため削除しない（{@link GameRepository#deleteAbandoned} 参照）。
 *
 * <p>「どれだけ放置されたら消すか」という保持方針をこの層が所有し、
 * 具体的な削除は outbound port（{@link GameRepository}）へ委ねる。
 */
@Service
public class AbandonedGameCleaner implements CleanUpAbandonedGamesUseCase {

    private final GameRepository gameRepository;
    private final Duration ttl;

    public AbandonedGameCleaner(
            GameRepository gameRepository,
            @Value("${app.game.abandoned-ttl:PT24H}") Duration ttl
    ) {
        this.gameRepository = gameRepository;
        this.ttl = ttl;
    }

    @Override
    @Transactional
    public int cleanUp() {
        OffsetDateTime threshold = OffsetDateTime.now().minus(ttl);

        return gameRepository.deleteAbandoned(threshold);
    }
}
