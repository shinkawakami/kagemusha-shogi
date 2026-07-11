package com.kagemusha.backend.domain.sfen;

import com.kagemusha.backend.domain.Board;
import com.kagemusha.backend.domain.CapturedPieces;
import com.kagemusha.backend.domain.Piece;
import com.kagemusha.backend.domain.PieceType;
import com.kagemusha.backend.domain.PlayerType;
import com.kagemusha.backend.domain.Position;

public class SfenConverter {

    /**
     * 持ち駒をSFENに出力するときの順番。
     *
     * SFENでは、先手の持ち駒を大文字、後手の持ち駒を小文字で表す。
     */
    private static final PieceType[] CAPTURED_PIECE_ORDER = {
            PieceType.GYOKU,
            PieceType.HISHA,
            PieceType.KAKU,
            PieceType.KIN,
            PieceType.GIN,
            PieceType.KEIMA,
            PieceType.KYO,
            PieceType.FU
    };

    private SfenConverter() {
    }

    /**
     * SFEN文字列から盤面情報だけを取り出し、Board に変換する。
     *
     * @param sfen SFEN文字列
     * @return 変換後の盤面
     */
    public static Board toBoard(String sfen) {
        String[] parts = splitSfen(sfen);

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
        String[] parts = splitSfen(sfen);

        if (parts.length < 2) {
            throw new IllegalArgumentException("SFENに手番情報がありません");
        }

        return parseTurn(parts[1]);
    }

    /**
     * SFEN文字列から持ち駒情報を取得する。
     *
     * 例:
     * -      持ち駒なし
     * P      先手の歩1枚
     * 2P     先手の歩2枚
     * R2Pb   先手の飛車1枚、先手の歩2枚、後手の角1枚
     *
     * @param sfen SFEN文字列
     * @return 持ち駒情報
     */
    public static CapturedPieces extractCapturedPieces(String sfen) {
        String[] parts = splitSfen(sfen);

        if (parts.length < 3) {
            throw new IllegalArgumentException("SFENに持ち駒情報がありません");
        }

        CapturedPieces capturedPieces = new CapturedPieces();

        String capturedPart = parts[2];

        if ("-".equals(capturedPart)) {
            return capturedPieces;
        }

        int count = 0;

        for (int i = 0; i < capturedPart.length(); i++) {
            char current = capturedPart.charAt(i);

            if (Character.isDigit(current)) {
                int digit = Character.getNumericValue(current);

                if (digit == 0 && count == 0) {
                    throw new IllegalArgumentException("SFENの持ち駒枚数が不正です: " + capturedPart);
                }

                count = count * 10 + digit;
                continue;
            }

            PlayerType owner = Character.isUpperCase(current)
                    ? PlayerType.SENTE
                    : PlayerType.GOTE;

            PieceType pieceType = SfenPieceSymbol.fromSymbol(String.valueOf(current));

            if (pieceType == PieceType.GYOKU) {
                throw new IllegalArgumentException("王は持ち駒にできません: " + capturedPart);
            }

            int amount = count == 0 ? 1 : count;

            for (int j = 0; j < amount; j++) {
                capturedPieces.add(owner, pieceType);
            }

            count = 0;
        }

        if (count != 0) {
            throw new IllegalArgumentException("SFENの持ち駒情報が不正です: " + capturedPart);
        }

        return capturedPieces;
    }

    /**
     * SFEN文字列から手数を取得する。
     *
     * @param sfen SFEN文字列
     * @return 手数
     */
    public static int extractMoveNumber(String sfen) {
        String[] parts = splitSfen(sfen);

        if (parts.length < 4) {
            throw new IllegalArgumentException("SFENに手数情報がありません");
        }

        try {
            return Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("SFENの手数が不正です: " + parts[3], e);
        }
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
        String turnPart = turnSymbol(currentTurn);
        String capturedPart = toSfenCapturedPieces(capturedPieces);

        return boardPart + " " + turnPart + " " + capturedPart + " " + moveNumber;
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

        for (int rowIndex = 0; rowIndex < Board.SIZE; rowIndex++) {
            if (rowIndex > 0) {
                result.append("/");
            }

            int emptyCount = 0;

            for (int colIndex = 0; colIndex < Board.SIZE; colIndex++) {
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

                if (piece.isPromoted() && !piece.getType().canPromote()) {
                    throw new IllegalArgumentException("成れない駒が成り状態になっています: " + piece.getType());
                }

                result.append(toSfenSymbol(piece));
            }

            if (emptyCount > 0) {
                result.append(emptyCount);
            }
        }

        return result.toString();
    }

    /**
     * SFEN文字列を空白で分割する。
     *
     * @param sfen SFEN文字列
     * @return 分割後の配列
     */
    private static String[] splitSfen(String sfen) {
        if (sfen == null || sfen.isBlank()) {
            throw new IllegalArgumentException("SFENが空です");
        }

        return sfen.trim().split("\\s+");
    }

    /**
     * 持ち駒情報をSFEN形式に変換する。
     *
     * @param capturedPieces 持ち駒
     * @return SFEN形式の持ち駒情報
     */
    private static String toSfenCapturedPieces(CapturedPieces capturedPieces) {
        if (capturedPieces == null) {
            return "-";
        }

        StringBuilder result = new StringBuilder();

        for (PieceType pieceType : CAPTURED_PIECE_ORDER) {
            appendCapturedPiece(result, capturedPieces, PlayerType.SENTE, pieceType);
        }

        for (PieceType pieceType : CAPTURED_PIECE_ORDER) {
            appendCapturedPiece(result, capturedPieces, PlayerType.GOTE, pieceType);
        }

        if (result.length() == 0) {
            return "-";
        }

        return result.toString();
    }

    /**
     * 指定した持ち駒をSFEN文字列に追加する。
     *
     * @param result 追加先
     * @param capturedPieces 持ち駒
     * @param owner 所有者
     * @param pieceType 駒種
     */
    private static void appendCapturedPiece(
            StringBuilder result,
            CapturedPieces capturedPieces,
            PlayerType owner,
            PieceType pieceType
    ) {
        int count = capturedPieces.count(owner, pieceType);

        if (count <= 0) {
            return;
        }

        if (count > 1) {
            result.append(count);
        }

        String symbol = SfenPieceSymbol.of(pieceType);

        if (owner == PlayerType.GOTE) {
            symbol = symbol.toLowerCase();
        }

        result.append(symbol);
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
        if (boardPart == null || boardPart.isBlank()) {
            throw new IllegalArgumentException("SFENの盤面情報が空です");
        }

        Board board = new Board();

        String[] rows = boardPart.split("/");

        if (rows.length != Board.SIZE) {
            throw new IllegalArgumentException("SFENの盤面行数が不正です: " + boardPart);
        }

        for (int rowIndex = 0; rowIndex < Board.SIZE; rowIndex++) {
            String rowText = rows[rowIndex];
            int colIndex = 0;

            for (int i = 0; i < rowText.length(); i++) {
                char current = rowText.charAt(i);

                // 数字の場合は空マスの数を表すため、colIndexを進める
                if (Character.isDigit(current)) {
                    int emptyCount = Character.getNumericValue(current);

                    if (emptyCount <= 0 || colIndex + emptyCount > Board.SIZE) {
                        throw new IllegalArgumentException("SFENの空マス数が不正です: " + rowText);
                    }

                    colIndex += emptyCount;
                    continue;
                }

                boolean promoted = false;

                // 成り駒の指定がある場合は、次の文字を取得する
                if (current == '+') {
                    promoted = true;
                    i++;

                    if (i >= rowText.length()) {
                        throw new IllegalArgumentException("成り駒の指定が不正です: " + rowText);
                    }

                    current = rowText.charAt(i);
                }

                if (colIndex >= Board.SIZE) {
                    throw new IllegalArgumentException("SFENの列数が不正です: " + rowText);
                }

                String symbol = String.valueOf(current);
                PieceType type = SfenPieceSymbol.fromSymbol(symbol);

                if (promoted && !type.canPromote()) {
                    throw new IllegalArgumentException("成れない駒が成り指定されています: " + rowText);
                }

                PlayerType owner = Character.isUpperCase(current)
                        ? PlayerType.SENTE
                        : PlayerType.GOTE;

                Piece piece = new Piece(type, owner, promoted);
                Position position = Position.fromArrayIndex(rowIndex, colIndex);

                board.setPiece(position, piece);

                colIndex++;
            }

            if (colIndex != Board.SIZE) {
                throw new IllegalArgumentException("SFENの列数が不正です: " + rowText);
            }
        }

        return board;
    }

    /**
     * 駒を SFEN の駒文字に変換する。
     *
     * <p>先手は大文字、後手は小文字。成り駒は先頭に {@code +} を付ける。
     * 例: 先手歩は {@code P}、後手歩は {@code p}、先手成歩は {@code +P}。
     */
    private static String toSfenSymbol(Piece piece) {
        String symbol = SfenPieceSymbol.of(piece.getType());

        if (piece.getOwner() == PlayerType.GOTE) {
            symbol = symbol.toLowerCase();
        }

        return piece.isPromoted() ? "+" + symbol : symbol;
    }

    /**
     * SFEN の手番文字（{@code b}/{@code w}）を手番に変換する。
     *
     * @throws IllegalArgumentException {@code b} または {@code w} 以外の場合
     */
    private static PlayerType parseTurn(String turn) {
        if ("b".equals(turn)) {
            return PlayerType.SENTE;
        }

        if ("w".equals(turn)) {
            return PlayerType.GOTE;
        }

        throw new IllegalArgumentException("不正な手番です: " + turn);
    }

    /**
     * 手番を SFEN の手番文字（先手 {@code b} / 後手 {@code w}）に変換する。
     */
    private static String turnSymbol(PlayerType playerType) {
        return playerType == PlayerType.SENTE ? "b" : "w";
    }
}