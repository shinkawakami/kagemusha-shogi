package com.kagemusha.backend.service;

import org.springframework.stereotype.Service;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.domain.PlayerType;

@Service
public class GameService {

    public Game getGame(Long id) {
        Board board = Board.createInitialBoard();

        return new Game(
                id,
                board,
                PlayerType.SENTE,
                GameStatus.PLAYING
        );
    }
}