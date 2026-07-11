package com.kagemusha.backend.infrastructure.messaging;

import com.kagemusha.backend.domain.FinishReason;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;

import java.util.UUID;

public class GameEvent {

    private GameEventType type;
    private UUID gameId;
    private GameStatus status;
    private PlayerType currentTurn;
    private PlayerType selectedPlayer;
    private String lastMove;
    private PlayerType winner;
    private FinishReason finishReason;

    public GameEvent(
            GameEventType type,
            UUID gameId,
            GameStatus status,
            PlayerType currentTurn,
            PlayerType selectedPlayer,
            String lastMove,
            PlayerType winner,
            FinishReason finishReason
    ) {
        this.type = type;
        this.gameId = gameId;
        this.status = status;
        this.currentTurn = currentTurn;
        this.selectedPlayer = selectedPlayer;
        this.lastMove = lastMove;
        this.winner = winner;
        this.finishReason = finishReason;
    }

    public GameEventType getType() {
        return type;
    }

    public UUID getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public PlayerType getCurrentTurn() {
        return currentTurn;
    }

    public PlayerType getSelectedPlayer() {
        return selectedPlayer;
    }

    public String getLastMove() {
        return lastMove;
    }

    public PlayerType getWinner() {
        return winner;
    }

    public FinishReason getFinishReason() {
        return finishReason;
    }
}