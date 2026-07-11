package com.kagemusha.backend.controller.response;

import com.kagemusha.backend.domain.FinishReason;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;
import com.kagemusha.backend.domain.sfen.SfenPositionConverter;

import java.util.UUID;

public class GameResponse {

    private UUID gameId;

    /**
     * 対局状態全体のSFEN文字列（盤面・手番・持ち駒・手数を含む）。
     *
     * フロントエンドはこの文字列をパースして盤面・手番・持ち駒などを描画する。
     */
    private String sfen;

    private GameStatus status;

    private PlayerType myPlayerType;

    /**
     * 自分の影武者の位置（SFEN/USI形式の座標文字列。例 "7g"）。
     *
     * 未選択の場合は null。相手の影武者位置は返さない。
     */
    private String myShadowPosition;

    private boolean senteShadowSelected;
    private boolean goteShadowSelected;

    private PlayerType winner;
    private FinishReason finishReason;

    /**
     * オフライン対局用レスポンス。
     *
     * オフラインでは userToken がないので、
     * myPlayerType / myShadowPosition は返さない。
     *
     * 影武者が選択済みかどうかは senteShadowSelected / goteShadowSelected で表す。
     */
    public static GameResponse fromOffline(Game game) {
        GameResponse response = new GameResponse();

        response.gameId = game.getId();
        response.sfen = game.getSfen();
        response.status = game.getStatus();

        response.myPlayerType = null;
        response.myShadowPosition = null;

        response.senteShadowSelected = game.getSenteShadowPosition() != null;
        response.goteShadowSelected = game.getGoteShadowPosition() != null;

        response.winner = game.getWinner();
        response.finishReason = game.getFinishReason();

        return response;
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

        Position myShadowPosition = myPlayerType == PlayerType.SENTE
                ? game.getSenteShadowPosition()
                : game.getGoteShadowPosition();

        GameResponse response = new GameResponse();

        response.gameId = game.getId();
        response.sfen = game.getSfen();
        response.status = game.getStatus();

        response.myPlayerType = myPlayerType;
        response.myShadowPosition = myShadowPosition == null
                ? null
                : SfenPositionConverter.toSfen(myShadowPosition);

        response.senteShadowSelected = game.getSenteShadowPosition() != null;
        response.goteShadowSelected = game.getGoteShadowPosition() != null;

        response.winner = game.getWinner();
        response.finishReason = game.getFinishReason();

        return response;
    }

    public UUID getGameId() {
        return gameId;
    }

    public String getSfen() {
        return sfen;
    }

    public GameStatus getStatus() {
        return status;
    }

    public PlayerType getMyPlayerType() {
        return myPlayerType;
    }

    public String getMyShadowPosition() {
        return myShadowPosition;
    }

    public boolean isSenteShadowSelected() {
        return senteShadowSelected;
    }

    public boolean isGoteShadowSelected() {
        return goteShadowSelected;
    }

    public PlayerType getWinner() {
        return winner;
    }

    public FinishReason getFinishReason() {
        return finishReason;
    }
}
