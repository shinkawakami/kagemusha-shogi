package com.kagemusha.backend.domain;

public class SfenConverter {

    private SfenConverter() {
    }

    public static Board toBoard(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 1) {
            throw new IllegalArgumentException("不正なSFENです: " + sfen);
        }

        String boardPart = parts[0];
        String[] rows = boardPart.split("/");

        if (rows.length != 9) {
            throw new IllegalArgumentException("SFENの行数が不正です: " + boardPart);
        }

        Board board = new Board();

        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            String rowText = rows[rowIndex];
            int colIndex = 0;

            for (int i = 0; i < rowText.length(); i++) {
                char ch = rowText.charAt(i);

                if (Character.isDigit(ch)) {
                    colIndex += Character.getNumericValue(ch);
                    continue;
                }

                boolean promoted = false;

                if (ch == '+') {
                    promoted = true;
                    i++;
                    ch = rowText.charAt(i);
                }

                if (colIndex >= 9) {
                    throw new IllegalArgumentException("SFENの列数が不正です: " + rowText);
                }

                String symbol = String.valueOf(ch);
                PieceType type = PieceType.fromSfenSymbol(symbol);
                PlayerType owner = Character.isUpperCase(ch)
                        ? PlayerType.SENTE
                        : PlayerType.GOTE;

                Position position = Position.fromArrayIndex(rowIndex, colIndex);
                board.setPiece(position, new Piece(type, owner, promoted));

                colIndex++;
            }

            if (colIndex != 9) {
                throw new IllegalArgumentException("SFENの列数が9ではありません: " + rowText);
            }
        }

        return board;
    }

    public static String fromBoard(Board board, PlayerType currentTurn) {
        StringBuilder sb = new StringBuilder();

        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            int emptyCount = 0;

            for (int col = 9; col >= 1; col--) {
                Position position = new Position(rowIndex + 1, col);
                Piece piece = board.getPiece(position);

                if (piece == null) {
                    emptyCount++;
                    continue;
                }

                if (emptyCount > 0) {
                    sb.append(emptyCount);
                    emptyCount = 0;
                }

                sb.append(piece.toSfenSymbol());
            }

            if (emptyCount > 0) {
                sb.append(emptyCount);
            }

            if (rowIndex < 8) {
                sb.append("/");
            }
        }

        sb.append(" ");
        sb.append(currentTurn.toSfenTurn());
        sb.append(" - 1");

        return sb.toString();
    }

    public static PlayerType extractCurrentTurn(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 2) {
            throw new IllegalArgumentException("手番情報がありません: " + sfen);
        }

        return PlayerType.fromSfenTurn(parts[1]);
    }

    public static String fromBoardOnly(Board board) {
        String fullSfen = fromBoard(board, PlayerType.SENTE);
        return fullSfen.split(" ")[0];
    }
}