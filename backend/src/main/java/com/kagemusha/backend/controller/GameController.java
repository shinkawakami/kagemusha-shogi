package com.kagemusha.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kagemusha.backend.controller.request.MoveRequest;
import com.kagemusha.backend.controller.response.GameData;
import com.kagemusha.backend.controller.response.GameResponse;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.service.GameService;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * 新しいゲーム作成
     */
    @PostMapping
    public ResponseEntity<GameResponse> createGame() {

        // 新しいゲームの作成する
        Game game = gameService.createGame();

        // APIレスポンスとして返すデータを作成する
        // Gameオブジェクトをそのまま返さず、必要な項目だけDTOに詰め替える
        GameData data = new GameData(
                game.getId(),
                game.getStatus().name(),
                null,
                game.getBoardSfen(),
                null);

        // 成功フラグとゲームデータをレスポンスとして返す
        // ResponseEntity.ok() により HTTP 200 OK で返却される
        return ResponseEntity.ok(new GameResponse(true, data));
    }

    /**
     * ゲーム取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        Game game = gameService.getGame(id);

        GameData data = new GameData(
                game.getId(),
                game.getStatus().name(),
                game.getWinner() == null ? null : game.getWinner().name(),
                game.getBoardSfen(),
                null);

        return ResponseEntity.ok(new GameResponse(true, data));
    }

    /**
     * 駒移動
     */
    @PostMapping("/{id}/moves")
    public ResponseEntity<GameResponse> move(
            @PathVariable Long id,
            @RequestBody MoveRequest request) {
        Game game = gameService.move(id, request.getMove());

        GameData data = new GameData(
                game.getId(),
                game.getStatus().name(),
                game.getWinner() == null ? null : game.getWinner().name(),
                game.getBoardSfen(),
                request.getMove());

        return ResponseEntity.ok(new GameResponse(true, data));
    }
}