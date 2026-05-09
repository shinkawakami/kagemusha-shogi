package com.kagemusha.backend.service;

import org.springframework.stereotype.Service;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.Game;

@Service
public class GameService {

    public Game createGame() {
        Board board = new Board();
        return new Game(1L, board);
    }
}