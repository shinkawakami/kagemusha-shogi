package com.kagemusha.backend.infrastructure.messaging;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.port.GameEventPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * {@link GameEventPort} の STOMP/WebSocket 実装（outbound adapter）。
 *
 * <p>対局イベントを {@code /topic/games/{gameId}} へ配信する。
 */
@Service
public class StompGameEventPublisher implements GameEventPort {

    private final SimpMessagingTemplate messagingTemplate;

    public StompGameEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publishPlayerJoined(Game game) {
        GameEvent event = new GameEvent(
                GameEventType.PLAYER_JOINED,
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

    @Override
    public void publishShadowSelected(Game game, PlayerType selectedPlayer) {
        GameEvent event = new GameEvent(
                GameEventType.SHADOW_SELECTED,
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

    @Override
    public void publishGameStarted(Game game) {
        GameEvent event = new GameEvent(
                GameEventType.GAME_STARTED,
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

    @Override
    public void publishMove(Game game, String lastMove) {
        GameEvent event = new GameEvent(
                GameEventType.MOVE,
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

    @Override
    public void publishGameFinished(Game game) {
        GameEvent event = new GameEvent(
                GameEventType.GAME_FINISHED,
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

    private void publish(UUID gameId, GameEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/games/" + gameId,
                event
        );
    }
}
