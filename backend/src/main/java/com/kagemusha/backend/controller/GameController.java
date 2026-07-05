package com.kagemusha.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kagemusha.backend.controller.request.MoveRequest;
import com.kagemusha.backend.controller.request.ResignRequest;
import com.kagemusha.backend.controller.request.SelectShadowRequest;
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
        Game game = gameService.createGame();
        // 成功フラグとゲームデータをレスポンス（ResponseEntity.ok() により HTTP 200 OK で返却）
        return ResponseEntity.ok(toResponse(game));
    }

    /**
     * ゲーム取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        Game game = gameService.getGame(id);
        return ResponseEntity.ok(toResponse(game));
    }

    /**
     * 影武者選択
     */
    @PostMapping("/{id}/shadow")
    public ResponseEntity<GameResponse> selectShadow(
            @PathVariable Long id,
            @RequestBody SelectShadowRequest request
    ) {
        Game game = gameService.selectShadow(id, request);
        return ResponseEntity.ok(toResponse(game));
    }

    /**
     * 駒移動
     */
    @PostMapping("/{id}/moves")
    public ResponseEntity<GameResponse> move(
            @PathVariable Long id,
            @RequestBody MoveRequest request) {
        Game game = gameService.move(id, request.getMove());
        return ResponseEntity.ok(toResponse(game));
    }

    /**
     * 投了
     */
    @PostMapping("/{gameId}/resign")
    public ResponseEntity<GameResponse> resign(
            @PathVariable Long gameId,
            @RequestBody ResignRequest request
    ) {
        Game game = gameService.resign(gameId, request);
        return ResponseEntity.ok(toResponse(game));
    }

    /**
     * GameオブジェクトをGameResponseに変換するヘルパーメソッド
     */
    private GameResponse toResponse(Game game) {
        GameData data = new GameData(
                game.getId(),
                game.getStatus().name(),
                game.getWinner() == null ? null : game.getWinner().name(),
                game.getSfen()
        );

        return new GameResponse(true, data);
    }
}