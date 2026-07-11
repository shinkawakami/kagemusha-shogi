package com.kagemusha.backend.controller;

import com.kagemusha.backend.controller.request.MoveRequest;
import com.kagemusha.backend.controller.request.SelectShadowRequest;
import com.kagemusha.backend.controller.response.GameResponse;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/online/games")
public class OnlineGameController {

    private static final String USER_TOKEN_HEADER = "X-User-Token";

    private final GameService gameService;

    public OnlineGameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * オンライン対局を作成する。
     *
     * 作成者は先手として登録する。
     *
     * Header:
     * X-User-Token: player-a
     */
    @PostMapping
    public GameResponse createOnlineGame(
            @RequestHeader(USER_TOKEN_HEADER) String userToken
    ) {
        Game game = gameService.createOnlineGame(userToken);

        return GameResponse.fromOnline(game, userToken);
    }

    /**
     * オンライン対局に参加する。
     *
     * 参加者は後手として登録する。
     *
     * Header:
     * X-User-Token: player-b
     */
    @PostMapping("/{gameId}/join")
    public GameResponse joinOnlineGame(
            @PathVariable UUID gameId,
            @RequestHeader(USER_TOKEN_HEADER) String userToken
    ) {
        Game game = gameService.joinOnlineGame(
                gameId,
                userToken
        );

        return GameResponse.fromOnline(game, userToken);
    }

    /**
     * オンライン対局の現在状態を取得する。
     *
     * userTokenから自分が先手か後手かを判定し、
     * 自分の影武者位置だけ返す。
     * 相手の影武者位置は返さない。
     */
    @GetMapping("/{gameId}")
    public GameResponse getOnlineGame(
            @PathVariable UUID gameId,
            @RequestHeader(USER_TOKEN_HEADER) String userToken
    ) {
        Game game = gameService.getGame(gameId);

        return GameResponse.fromOnline(
                game,
                userToken
        );
    }

    /**
     * オンライン対局で影武者を選択する。
     *
     * userTokenから先手・後手を判定するため、
     * URLに playerType は含めない。
     */
    @PostMapping("/{gameId}/shadow")
    public GameResponse selectShadow(
            @PathVariable UUID gameId,
            @RequestHeader(USER_TOKEN_HEADER) String userToken,
            @RequestBody SelectShadowRequest request
    ) {
        Game game = gameService.selectShadowOnline(
                gameId,
                userToken,
                request.getPosition()
        );

        return GameResponse.fromOnline(
                game,
                userToken
        );
    }

    /**
     * オンライン対局で指し手を実行する。
     *
     * userTokenからプレイヤーを判定し、
     * 自分の手番でなければエラーにする。
     */
    @PostMapping("/{gameId}/moves")
    public GameResponse move(
            @PathVariable UUID gameId,
            @RequestHeader(USER_TOKEN_HEADER) String userToken,
            @RequestBody MoveRequest request
    ) {
        Game game = gameService.moveOnline(
                gameId,
                userToken,
                request.getMove()
        );

        return GameResponse.fromOnline(
                game,
                userToken
        );
    }

    /**
     * オンライン対局で投了する。
     *
     * userTokenから投了者を判定する。
     */
    @PostMapping("/{gameId}/resign")
    public GameResponse resign(
            @PathVariable UUID gameId,
            @RequestHeader(USER_TOKEN_HEADER) String userToken
    ) {
        Game game = gameService.resignOnline(
                gameId,
                userToken
        );

        return GameResponse.fromOnline(
                game,
                userToken
        );
    }
}