package com.kagemusha.backend.controller.response;

import com.kagemusha.backend.domain.FinishReason;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenPositionConverter;

import java.util.UUID;

/**
 * 対局状態のレスポンス DTO。
 *
 * @param gameId              対局ID
 * @param sfen                対局状態全体のSFEN文字列（盤面・手番・持ち駒・手数を含む）。
 *                            フロントエンドはこれをパースして描画する。
 * @param status              対局ステータス
 * @param myPlayerType        自分が先手か後手か。オフラインでは {@code null}。
 * @param myShadowPosition    自分の影武者位置（SFEN/USI形式。例 "7g"）。未選択・オフラインでは {@code null}。
 *                            相手の影武者位置は返さない。
 * @param senteShadowSelected 先手が影武者を選択済みか
 * @param goteShadowSelected  後手が影武者を選択済みか
 * @param winner              勝者。未決着なら {@code null}。
 * @param finishReason        終局理由。未決着なら {@code null}。
 */
public record GameResponse(
        UUID gameId,
        String sfen,
        GameStatus status,
        PlayerType myPlayerType,
        String myShadowPosition,
        boolean senteShadowSelected,
        boolean goteShadowSelected,
        PlayerType winner,
        FinishReason finishReason
) {

    /**
     * オフライン対局用レスポンス。
     *
     * オフラインでは userToken がないので、
     * myPlayerType / myShadowPosition は返さない。
     *
     * 影武者が選択済みかどうかは senteShadowSelected / goteShadowSelected で表す。
     */
    public static GameResponse fromOffline(Game game) {
        return new GameResponse(
                game.getId(),
                game.getSfen(),
                game.getStatus(),
                null,
                null,
                game.getSenteShadowPosition() != null,
                game.getGoteShadowPosition() != null,
                game.getWinner(),
                game.getFinishReason()
        );
    }

    /**
     * オンライン対局用レスポンス。
     *
     * userToken から自分が先手か後手か判定し、
     * 自分の影武者位置だけ返す。
     *
     * 相手の影武者位置は絶対に返さない。
     */
    public static GameResponse fromOnline(Game game, String userToken) {
        PlayerType myPlayerType = game.resolvePlayerType(userToken);

        Position myShadowPosition = game.getShadowPosition(myPlayerType);

        return new GameResponse(
                game.getId(),
                game.getSfen(),
                game.getStatus(),
                myPlayerType,
                myShadowPosition == null ? null : SfenPositionConverter.toSfen(myShadowPosition),
                game.getSenteShadowPosition() != null,
                game.getGoteShadowPosition() != null,
                game.getWinner(),
                game.getFinishReason()
        );
    }
}
