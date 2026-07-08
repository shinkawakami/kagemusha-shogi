package com.kagemusha.controller;

import com.kagemusha.dto.SampleBoardResponse;
import com.kagemusha.dto.SamplePieceResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SampleBoardController {

    @GetMapping("/api/sample-board")
    public SampleBoardResponse getSampleBoard() {
        List<List<SamplePieceResponse>> board = new ArrayList<>();

        for (int row = 0; row < 9; row++) {
            List<SamplePieceResponse> line = new ArrayList<>();

            for (int col = 0; col < 9; col++) {
                line.add(new SamplePieceResponse("", "", "", row, col));
            }

            board.add(line);
        }

        board.get(0).set(4, new SamplePieceResponse("KING", "GOTE", "王", 0, 4));
        board.get(6).set(4, new SamplePieceResponse("PAWN", "SENTE", "歩", 6, 4));

        return new SampleBoardResponse(board);
    }
}