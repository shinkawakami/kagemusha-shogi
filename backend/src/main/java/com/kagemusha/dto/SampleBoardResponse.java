package com.kagemusha.dto;

import java.util.List;

public class SampleBoardResponse {

    private List<List<SamplePieceResponse>> board;

    public SampleBoardResponse() {
    }

    public SampleBoardResponse(List<List<SamplePieceResponse>> board) {
        this.board = board;
    }

    public List<List<SamplePieceResponse>> getBoard() {
        return board;
    }

    public void setBoard(List<List<SamplePieceResponse>> board) {
        this.board = board;
    }
}