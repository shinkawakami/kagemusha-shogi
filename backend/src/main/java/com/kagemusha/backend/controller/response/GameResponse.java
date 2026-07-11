package com.kagemusha.backend.controller.response;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.FinishReason;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;

public class GameResponse {

    private Long gameId;
    private Board board;
    private PlayerType currentTurn;
    private GameStatus status;
    private int moveNumber;

    private PlayerType myPlayerType;
    private Position myShadowPosition;
    private boolean myShadowSelected;
    private boolean opponentShadowSelected;

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
     * 影武者の位置も返さない。
     * 返すのは「選択済みかどうか」だけ。
     */
    public static GameResponse fromOffline(Game game) {
        GameResponse response = new GameResponse();

        response.gameId = game.getId();
        response.board = game.getBoard();
        response.currentTurn = game.getCurrentTurn();
        response.status = game.getStatus();
        response.moveNumber = game.getMoveNumber();

        response.myPlayerType = null;
        response.myShadowPosition = null;
        response.myShadowSelected = false;
        response.opponentShadowSelected = false;

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

        Position myShadowPosition;
        Position opponentShadowPosition;

        if (myPlayerType == PlayerType.SENTE) {
            myShadowPosition = game.getSenteShadowPosition();
            opponentShadowPosition = game.getGoteShadowPosition();
        } else {
            myShadowPosition = game.getGoteShadowPosition();
            opponentShadowPosition = game.getSenteShadowPosition();
        }

        GameResponse response = new GameResponse();

        response.gameId = game.getId();
        response.board = game.getBoard();
        response.currentTurn = game.getCurrentTurn();
        response.status = game.getStatus();
        response.moveNumber = game.getMoveNumber();

        response.myPlayerType = myPlayerType;
        response.myShadowPosition = myShadowPosition;
        response.myShadowSelected = myShadowPosition != null;
        response.opponentShadowSelected = opponentShadowPosition != null;

        response.senteShadowSelected = game.getSenteShadowPosition() != null;
        response.goteShadowSelected = game.getGoteShadowPosition() != null;

        response.winner = game.getWinner();
        response.finishReason = game.getFinishReason();

        return response;
    }

    public Long getGameId() {
        return gameId;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerType getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public PlayerType getMyPlayerType() {
        return myPlayerType;
    }

    public Position getMyShadowPosition() {
        return myShadowPosition;
    }

    public boolean isMyShadowSelected() {
        return myShadowSelected;
    }

    public boolean isOpponentShadowSelected() {
        return opponentShadowSelected;
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