package com.kagemusha.backend.infrastructure.messaging;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.port.out.GameEventPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link GameEventPort} の STOMP/WebSocket 実装（outbound adapter）。
 *
 * <p>対局イベントを {@code /topic/games/{gameId}} へ配信する。
 */
@Component
public class StompGameEventPublisher implements GameEventPort {

    private final SimpMessagingTemplate messagingTemplate;

    public StompGameEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publishPlayerJoined(Game game) {
        publish(game.getId(), GameEvent.playerJoined(game));
    }

    @Override
    public void publishShadowSelected(Game game, PlayerType selectedPlayer) {
        publish(game.getId(), GameEvent.shadowSelected(game, selectedPlayer));
    }

    @Override
    public void publishGameStarted(Game game) {
        publish(game.getId(), GameEvent.gameStarted(game));
    }

    @Override
    public void publishMove(Game game, String lastMove) {
        publish(game.getId(), GameEvent.move(game, lastMove));
    }

    @Override
    public void publishGameFinished(Game game) {
        publish(game.getId(), GameEvent.gameFinished(game));
    }

    private void publish(UUID gameId, GameEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/games/" + gameId,
                event
        );
    }
}
