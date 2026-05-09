package com.kagemusha.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.service.GameService;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public Game createGame() {
        return gameService.createGame();
    }
}