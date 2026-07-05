package com.kagemusha.backend.service;

import com.kagemusha.backend.controller.request.ResignRequest;
import com.kagemusha.backend.controller.request.SelectShadowRequest;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenPositionConverter;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameService {

    /**
     * 作成されたゲームを保持するためのMap
     *
     * key   : ゲームID
     * value : Gameオブジェクト
     *
     * ConcurrentHashMapを使用しているため、
     * 複数のリクエストが同時に来ても比較的安全に扱える。
     *
     * ※現在はメモリ上に保存しているため、
     * アプリを再起動するとゲーム情報は消える。
     */
    private final ConcurrentHashMap<Long, Game> games = new ConcurrentHashMap<>();

    /**
     * ゲームIDを採番するためのカウンター
     *
     * AtomicLongを使用することで、
     * 複数リクエストが同時に来てもIDが重複しにくい。
     *
     * 初期値は1なので、最初に作成されるゲームIDは1になる。
     */
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * 新しいゲームを作成する
     */
    public Game createGame() {
        // 現在のカウンター値を取得し、その後カウンターを1増やす
        Long id = idCounter.getAndIncrement();
        // 採番したIDを使って、初期状態のゲームを作成する
        Game game = Game.createInitialGame(id);
        // 作成したゲームを、ゲームIDをキーとして保存する
        games.put(id, game);
        return game;
    }

    /**
     * ゲームIDを指定してゲームを取得する
     */
    public Game getGame(Long id) {
        // MapからゲームIDに対応するGameを取得する
        Game game = games.get(id);
        if (game == null) {
            throw new IllegalArgumentException("存在しないゲームIDです: " + id);
        }
        return game;
    }

    public Game selectShadow(Long gameId, SelectShadowRequest request) {
        Game game = getGame(gameId);

        Position position = SfenPositionConverter.toPosition(request.getPosition());

        game.selectShadow(request.getPlayerType(), position);

        return game;
    }

    /**
     * 指定されたゲームに対して駒移動を行う
     */
    public Game move(Long gameId, String moveText) {
        Game game = getGame(gameId);
        game.move(moveText);
        return game;
    }

    /**
     * 勝敗を確定する
     */
    public void finishGame(Long gameId, PlayerType winner) {
        Game game = getGame(gameId);
        game.finish(winner);
    }

    public Game resign(Long gameId, ResignRequest request) {
        Game game = getGame(gameId);
        game.resign(request.getPlayerType());
        return game;
    }
}