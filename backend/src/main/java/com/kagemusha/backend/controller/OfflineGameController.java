package com.kagemusha.backend.controller;

import com.kagemusha.backend.controller.request.MoveRequest;
import com.kagemusha.backend.controller.request.SelectShadowRequest;
import com.kagemusha.backend.controller.response.GameResponse;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offline/games")
public class OfflineGameController {

    private final GameService gameService;

    public OfflineGameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * オフライン対局を作成する。
     *
     * 1つのブラウザ上で先手・後手が交互に操作する対局。
     * userTokenやWebSocketは使わない。
     */
    @PostMapping
    public GameResponse createOfflineGame() {
        Game game = gameService.createOfflineGame();

        return GameResponse.fromOffline(game);
    }

    /**
     * オフライン対局の現在状態を取得する。
     */
    @GetMapping("/{gameId}")
    public GameResponse getOfflineGame(
            @PathVariable Long gameId
    ) {
        Game game = gameService.getGame(gameId);

        return GameResponse.fromOffline(game);
    }

    /**
     * オフライン対局で影武者を選択する。
     *
     * オフラインでは userToken がないため、
     * URLで SENTE / GOTE を指定する。
     *
     * 例:
     * POST /api/offline/games/1/shadow/SENTE
     * POST /api/offline/games/1/shadow/GOTE
     */
    @PostMapping("/{gameId}/shadow/{playerType}")
    public GameResponse selectShadow(
            @PathVariable Long gameId,
            @PathVariable PlayerType playerType,
            @RequestBody SelectShadowRequest request
    ) {
        Game game = gameService.selectShadowOffline(
                gameId,
                playerType,
                request.getPosition()
        );

        return GameResponse.fromOffline(game);
    }

    /**
     * オフライン対局で指し手を実行する。
     *
     * オフラインでは現在の手番のプレイヤーが指したものとして扱う。
     */
    @PostMapping("/{gameId}/moves")
    public GameResponse move(
            @PathVariable Long gameId,
            @RequestBody MoveRequest request
    ) {
        Game game = gameService.moveOffline(
                gameId,
                request.getMove()
        );

        return GameResponse.fromOffline(game);
    }

    /**
     * オフライン対局で投了する。
     *
     * オフラインでは userToken がないため、
     * URLで投了する側を指定する。
     *
     * 例:
     * POST /api/offline/games/1/resign/SENTE
     * POST /api/offline/games/1/resign/GOTE
     */
    @PostMapping("/{gameId}/resign/{playerType}")
    public GameResponse resign(
            @PathVariable Long gameId,
            @PathVariable PlayerType playerType
    ) {
        Game game = gameService.resignOffline(
                gameId,
                playerType
        );

        return GameResponse.fromOffline(game);
    }
}