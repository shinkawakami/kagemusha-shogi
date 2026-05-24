package com.kagemusha.backend.service;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameService {

    private final ConcurrentHashMap<Long, Game> games = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * 新しいゲームを作成
     */
    public Game createGame() {
        Long id = idCounter.getAndIncrement();
        Game game = Game.createInitialGame(id);
        games.put(id, game);
        return game;
    }

    /**
     * ゲーム取得
     */
    public Game getGame(Long id) {
        Game game = games.get(id);
        if (game == null) {
            throw new IllegalArgumentException("存在しないゲームIDです: " + id);
        }
        return game;
    }

    public Game move(Long gameId, String moveText) {
        Game game = getGame(gameId);
        game.move(moveText);
        return game;
    }

    /**
     * 勝敗を確定（後で影武者処理で使用）
     */
    public void finishGame(Long gameId, PlayerType winner) {
        Game game = getGame(gameId);
        game.finish(winner);
    }
}