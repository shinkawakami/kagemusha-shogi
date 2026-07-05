package com.kagemusha.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kagemusha.backend.controller.mapper.GameResponseMapper;
import com.kagemusha.backend.controller.request.LoseRequest;
import com.kagemusha.backend.controller.request.MoveRequest;
import com.kagemusha.backend.controller.request.SelectShadowRequest;
import com.kagemusha.backend.controller.response.ApiResponse;
import com.kagemusha.backend.controller.response.GameData;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.service.GameService;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;
    private final GameResponseMapper gameResponseMapper;

    public GameController(
            GameService gameService,
            GameResponseMapper gameResponseMapper
    ) {
        this.gameService = gameService;
        this.gameResponseMapper = gameResponseMapper;
    }

    /**
     * 新しいゲーム作成
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GameData>>  createGame() {
        Game game = gameService.createGame();
        GameData data = gameResponseMapper.toGameData(game);
        // 成功フラグとゲームデータをレスポンス（ResponseEntity.ok() により HTTP 200 OK で返却）
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * ゲーム取得
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<ApiResponse<GameData>> getGame(@PathVariable Long gameId) {
        Game game = gameService.getGame(gameId);
        GameData data = gameResponseMapper.toGameData(game);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 影武者選択
     */
    @PostMapping("/{gameId}/shadow")
    public ResponseEntity<ApiResponse<GameData>> selectShadow(
            @PathVariable Long gameId,
            @RequestBody SelectShadowRequest request
    ) {
        Game game = gameService.selectShadow(gameId, request);
        GameData data = gameResponseMapper.toGameData(game);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 駒移動
     */
    @PostMapping("/{gameId}/moves")
    public ResponseEntity<ApiResponse<GameData>> move(
            @PathVariable Long gameId,
            @RequestBody MoveRequest request
    ) {
        Game game = gameService.move(gameId, request.getMove());
        GameData data = gameResponseMapper.toGameData(game);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 敗北
     */
    @PostMapping("/{gameId}/lose")
    public ResponseEntity<ApiResponse<GameData>> lose(
            @PathVariable Long gameId,
            @RequestBody LoseRequest request
    ) {
        Game game = gameService.lose(gameId, request);
        GameData data = gameResponseMapper.toGameData(game, request.getFinishReason());

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}