package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.CapturedPieces;
import com.kagemusha.backend.domain.Piece;
import com.kagemusha.backend.domain.PieceType;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;

public class SfenConverter {

    /**
     * SFEN文字列から盤面情報だけを取り出し、Board に変換する。
     *
     * @param sfen SFEN文字列
     * @return 変換後の盤面
     */
    public static Board toBoard(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 1) {
            throw new IllegalArgumentException("SFENに盤面情報がありません");
        }

        return toBoardFromBoardPart(parts[0]);
    }

    /**
     * SFEN文字列から現在の手番を取得する。
     *
     * @param sfen SFEN文字列
     * @return 現在の手番
     */
    public static PlayerType extractCurrentTurn(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 2) {
            throw new IllegalArgumentException("SFENに手番情報がありません");
        }

        return PlayerType.fromSfenTurn(parts[1]);
    }

    /**
     * SFEN文字列から持ち駒情報を取得する。
     *
     * 現時点では持ち駒の詳細変換は未実装のため、
     * 空の CapturedPieces を返す。
     *
     * @param sfen SFEN文字列
     * @return 持ち駒情報
     */
    public static CapturedPieces extractCapturedPieces(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 3) {
            throw new IllegalArgumentException("SFENに持ち駒情報がありません");
        }

        CapturedPieces capturedPieces = new CapturedPieces();

        String capturedPart = parts[2];

        if (capturedPart.equals("-")) {
            return capturedPieces;
        }

        // 例: S2Pb3p
        // 持ち駒機能を実装するタイミングで、ここに変換処理を追加する。

        return capturedPieces;
    }

    /**
     * SFEN文字列から手数を取得する。
     *
     * @param sfen SFEN文字列
     * @return 手数
     */
    public static int extractMoveNumber(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 4) {
            throw new IllegalArgumentException("SFENに手数情報がありません");
        }

        return Integer.parseInt(parts[3]);
    }

    /**
     * 盤面、手番、持ち駒、手数からSFEN文字列を生成する。
     *
     * @param board 盤面
     * @param currentTurn 現在の手番
     * @param capturedPieces 持ち駒
     * @param moveNumber 手数
     * @return SFEN文字列
     */
    public static String from(
            Board board,
            PlayerType currentTurn,
            CapturedPieces capturedPieces,
            int moveNumber
    ) {
        String boardPart = fromBoardOnly(board);
        String turnPart = currentTurn.toSfenTurn();
        String capturedPart = toSfenCapturedPieces(capturedPieces);

        return boardPart + " " + turnPart + " " + capturedPart + " " + moveNumber;
    }

    /**
     * 持ち駒情報をSFEN形式に変換する。
     *
     * 現時点では持ち駒変換は未実装のため、持ち駒なしを表す "-" を返す。
     *
     * @param capturedPieces 持ち駒
     * @return SFEN形式の持ち駒情報
     */
    private static String toSfenCapturedPieces(CapturedPieces capturedPieces) {
        return "-";
    }

    /**
     * SFENの盤面部分を Board に変換する。
     *
     * 例:
     * lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL
     *
     * @param boardPart SFENの盤面部分
     * @return 変換後の盤面
     */
    private static Board toBoardFromBoardPart(String boardPart) {
        Board board = new Board();

        String[] rows = boardPart.split("/");

        if (rows.length != 9) {
            throw new IllegalArgumentException("SFENの盤面行数が不正です: " + boardPart);
        }

        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            String rowText = rows[rowIndex];
            int colIndex = 0;

            for (int i = 0; i < rowText.length(); i++) {
                char current = rowText.charAt(i);

                if (Character.isDigit(current)) {
                    int emptyCount = Character.getNumericValue(current);
                    colIndex += emptyCount;
                    continue;
                }

                boolean promoted = false;

                if (current == '+') {
                    promoted = true;
                    i++;

                    if (i >= rowText.length()) {
                        throw new IllegalArgumentException("成り駒の指定が不正です: " + rowText);
                    }

                    current = rowText.charAt(i);
                }

                if (colIndex >= 9) {
                    throw new IllegalArgumentException("SFENの列数が不正です: " + rowText);
                }

                String symbol = String.valueOf(current);
                PieceType type = PieceType.fromSfenSymbol(symbol);
                PlayerType owner = Character.isUpperCase(current)
                        ? PlayerType.SENTE
                        : PlayerType.GOTE;

                Piece piece = new Piece(type, owner, promoted);
                Position position = Position.fromArrayIndex(rowIndex, colIndex);

                board.setPiece(position, piece);

                colIndex++;
            }

            if (colIndex != 9) {
                throw new IllegalArgumentException("SFENの列数が不正です: " + rowText);
            }
        }

        return board;
    }

    /**
     * Board をSFENの盤面部分に変換する。
     *
     * 駒がないマスは連続数値としてまとめる。
     *
     * @param board 盤面
     * @return SFENの盤面部分
     */
    public static String fromBoardOnly(Board board) {
        StringBuilder result = new StringBuilder();

        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            if (rowIndex > 0) {
                result.append("/");
            }

            int emptyCount = 0;

            for (int colIndex = 0; colIndex < 9; colIndex++) {
                Position position = Position.fromArrayIndex(rowIndex, colIndex);
                Piece piece = board.getPiece(position);

                if (piece == null) {
                    emptyCount++;
                    continue;
                }

                if (emptyCount > 0) {
                    result.append(emptyCount);
                    emptyCount = 0;
                }

                result.append(piece.toSfenSymbol());
            }

            if (emptyCount > 0) {
                result.append(emptyCount);
            }
        }

        return result.toString();
    }
}