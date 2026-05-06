package com.kagemusha.dto;

public class SamplePieceResponse {

    private String type;
    private String owner;
    private String label;
    private int row;
    private int col;

    public SamplePieceResponse() {
    }

    public SamplePieceResponse(String type, String owner, String label, int row, int col) {
        this.type = type;
        this.owner = owner;
        this.label = label;
        this.row = row;
        this.col = col;
    }

    public String getType() {
        return type;
    }

    public String getOwner() {
        return owner;
    }

    public String getLabel() {
        return label;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }
}