package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.CapturedPieces;
import com.kagemusha.backend.domain.PlayerType;

public class SfenConverter {

    public static Board toBoard(String sfen) {
        String boardPart = sfen.split(" ")[0];

        // ここは既存の盤面変換処理を使う
        // boardPart を Board に変換する
        return toBoardFromBoardPart(boardPart);
    }

    public static PlayerType extractCurrentTurn(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 2) {
            throw new IllegalArgumentException("SFENに手番情報がありません");
        }

        return switch (parts[1]) {
            case "b" -> PlayerType.SENTE;
            case "w" -> PlayerType.GOTE;
            default -> throw new IllegalArgumentException("不正な手番です: " + parts[1]);
        };
    }

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
        // ここは後で持ち駒実装時に細かく作り込む
        // 現時点では空のCapturedPiecesを返しておくでもOK

        return capturedPieces;
    }

    public static int extractMoveNumber(String sfen) {
        String[] parts = sfen.split(" ");

        if (parts.length < 4) {
            throw new IllegalArgumentException("SFENに手数情報がありません");
        }

        return Integer.parseInt(parts[3]);
    }

    public static String from(
            Board board,
            PlayerType currentTurn,
            CapturedPieces capturedPieces,
            int moveNumber
    ) {
        String boardPart = fromBoardOnly(board);
        String turnPart = toSfenTurn(currentTurn);
        String capturedPart = toSfenCapturedPieces(capturedPieces);

        return boardPart + " " + turnPart + " " + capturedPart + " " + moveNumber;
    }

    private static String toSfenTurn(PlayerType currentTurn) {
        return switch (currentTurn) {
            case SENTE -> "b";
            case GOTE -> "w";
        };
    }

    private static String toSfenCapturedPieces(CapturedPieces capturedPieces) {
        // 持ち駒がない場合
        // 今の段階では一旦 "-" 固定でもOK
        return "-";
    }

    private static Board toBoardFromBoardPart(String boardPart) {
        // 既存の実装に合わせてください
        throw new UnsupportedOperationException("既存の盤面変換処理を使用してください");
    }

    public static String fromBoardOnly(Board board) {
        // 既存の実装に合わせてください
        throw new UnsupportedOperationException("既存の盤面SFEN変換処理を使用してください");
    }
}