package com.kagemusha.backend.websocket;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public GameEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishPlayerJoined(Game game) {
        GameEvent event = new GameEvent(
                GameEventType.PLAYER_JOINED.name(),
                game.getId(),
                game.getStatus(),
                game.getCurrentTurn(),
                null,
                null,
                null,
                null
        );

        publish(game.getId(), event);
    }

    public void publishShadowSelected(Game game, PlayerType selectedPlayer) {
        GameEvent event = new GameEvent(
                GameEventType.SHADOW_SELECTED.name(),
                game.getId(),
                game.getStatus(),
                game.getCurrentTurn(),
                selectedPlayer,
                null,
                null,
                null
        );

        publish(game.getId(), event);
    }

    public void publishGameStarted(Game game) {
        GameEvent event = new GameEvent(
                GameEventType.GAME_STARTED.name(),
                game.getId(),
                game.getStatus(),
                game.getCurrentTurn(),
                null,
                null,
                null,
                null
        );

        publish(game.getId(), event);
    }

    public void publishMove(Game game, String lastMove) {
        GameEvent event = new GameEvent(
                GameEventType.MOVE.name(),
                game.getId(),
                game.getStatus(),
                game.getCurrentTurn(),
                null,
                lastMove,
                null,
                null
        );

        publish(game.getId(), event);
    }

    public void publishGameFinished(Game game) {
        GameEvent event = new GameEvent(
                GameEventType.GAME_FINISHED.name(),
                game.getId(),
                game.getStatus(),
                game.getCurrentTurn(),
                null,
                null,
                game.getWinner(),
                game.getFinishReason()
        );

        publish(game.getId(), event);
    }

    private void publish(Long gameId, GameEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/games/" + gameId,
                event
        );
    }
}