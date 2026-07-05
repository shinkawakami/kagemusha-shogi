package com.kagemusha.backend.controller.mapper;

import org.springframework.stereotype.Component;

import com.kagemusha.backend.controller.response.GameData;
import com.kagemusha.backend.domain.Game;

@Component
public class GameResponseMapper {

    public GameData toGameData(Game game) {
        return toGameData(game, null);
    }

    public GameData toGameData(Game game, String finishReason) {
        return new GameData(
                game.getId(),
                game.getStatus().name(),
                game.getCurrentTurn() == null ? null : game.getCurrentTurn().name(),
                game.getSfen(),
                game.getWinner() == null ? null : game.getWinner().name(),
                finishReason
        );
    }
}