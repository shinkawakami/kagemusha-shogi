package com.kagemusha.backend.domain;

import com.kagemusha.backend.domain.sfen.SfenConstants;
import com.kagemusha.backend.domain.sfen.SfenConverter;
import com.kagemusha.backend.domain.sfen.SfenMove;
import com.kagemusha.backend.domain.sfen.SfenMoveParser;
import com.kagemusha.backend.domain.validator.DropMoveValidator;
import com.kagemusha.backend.domain.validator.MoveValidator;
import com.kagemusha.backend.domain.validator.PromotionValidator;

public class Game {

    private final Long id;
    private Board board;
    private PlayerType currentTurn;
    private CapturedPieces capturedPieces;
    private int moveNumber;
    private GameStatus status;
    private GameMode mode;

    private String senteUserToken;
    private String goteUserToken;

    private Position senteShadowPosition;
    private Position goteShadowPosition;

    private PlayerType winner;
    private FinishReason finishReason;

    public Game(
            Long id,
            Board board,
            PlayerType currentTurn,
            CapturedPieces capturedPieces,
            int moveNumber) {
        this.id = id;
        this.board = board;
        this.currentTurn = currentTurn;
        this.capturedPieces = capturedPieces;
        this.moveNumber = moveNumber;
    }

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerType getCurrentTurn() {
        return currentTurn;
    }

    public CapturedPieces getCapturedPieces() {
        return capturedPieces;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameMode getMode() {
        return mode;
    }

    public String getSenteUserToken() {
        return senteUserToken;
    }

    public String getGoteUserToken() {
        return goteUserToken;
    }

    public Position getSenteShadowPosition() {
        return senteShadowPosition;
    }

    public Position getGoteShadowPosition() {
        return goteShadowPosition;
    }

    public PlayerType getWinner() {
        return winner;
    }

    public FinishReason getFinishReason() {
        return finishReason;
    }

    /**
     * オフライン対局を初期状態で作成する。
     *
     * 生成時点で影武者選択状態にする。
     */
    public static Game createOffline(Long id) {
        Game game = createInitial(id);
        game.mode = GameMode.OFFLINE;
        game.status = GameStatus.SELECTING_SHADOW;

        return game;
    }

    /**
     * オンライン対局を初期状態で作成する。
     *
     * 作成者を先手として登録し、相手の参加を待つ状態にする。
     */
    public static Game createOnline(Long id, String senteUserToken) {
        Game game = createInitial(id);
        game.mode = GameMode.ONLINE;
        game.status = GameStatus.WAITING;
        game.senteUserToken = senteUserToken;

        return game;
    }

    /**
     * 初期局面の盤面・手番・持ち駒・手数を持つゲームを生成する。
     *
     * モード・状態・トークンは各ファクトリメソッドで確定させる。
     */
    private static Game createInitial(Long id) {
        Board board = SfenConverter.toBoard(SfenConstants.INITIAL_SFEN);
        PlayerType currentTurn = SfenConverter.extractCurrentTurn(SfenConstants.INITIAL_SFEN);
        CapturedPieces capturedPieces = SfenConverter.extractCapturedPieces(SfenConstants.INITIAL_SFEN);
        int moveNumber = SfenConverter.extractMoveNumber(SfenConstants.INITIAL_SFEN);

        return new Game(
                id,
                board,
                currentTurn,
                capturedPieces,
                moveNumber);
    }

    /**
     * オンライン対局に後手として参加する。
     *
     * 相手待ち状態のときだけ参加でき、参加後は影武者選択状態にする。
     */
    public void join(String goteUserToken) {
        if (status != GameStatus.WAITING) {
            throw new IllegalArgumentException("この対局には参加できません");
        }

        this.goteUserToken = goteUserToken;
        this.status = GameStatus.SELECTING_SHADOW;
    }

    /**
     * 駒を移動する
     */
    public void move(String moveText) {
        if (status != GameStatus.PLAYING) {
            throw new IllegalStateException("対局中ではありません");
        }

        SfenMove move = SfenMoveParser.parse(moveText);

        if (move.isDrop()) {
            DropMoveValidator.validate(this, move);

            capturedPieces.remove(currentTurn, move.getDropPieceType());

            Piece droppedPiece = new Piece(
                    move.getDropPieceType(),
                    currentTurn,
                    false
            );

            board.setPiece(move.getTo(), droppedPiece);

            switchTurn();
            moveNumber++;
            return;
        }

        MoveValidator.validate(this, move);

        Piece movingPiece = board.getPiece(move.getFrom());

        PromotionValidator.validate(movingPiece, move);

        Piece capturedPiece = board.getPiece(move.getTo());

        boolean capturedShadow = false;

        if (capturedPiece != null) {
            capturedShadow = isShadowPosition(
                    capturedPiece.getOwner(),
                    move.getTo()
            );

            capturedPieces.add(
                    movingPiece.getOwner(),
                    capturedPiece.getType()
            );
        }

        Piece pieceAfterMove = movingPiece;

        if (move.isPromote()) {
            pieceAfterMove = new Piece(
                    movingPiece.getType(),
                    movingPiece.getOwner(),
                    true
            );
        }

        board.setPiece(move.getTo(), pieceAfterMove);
        board.removePiece(move.getFrom());

        updateShadowPositionIfNeeded(
                movingPiece.getOwner(),
                move.getFrom(),
                move.getTo()
        );

        if (capturedShadow) {
            finish(movingPiece.getOwner(), FinishReason.SHADOW_CAPTURED);
            moveNumber++;
            return;
        }

        switchTurn();
        moveNumber++;
    }

    /**
     * ゲーム状態全体のSFENを返す。
     *
     * 例:
     * lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1
     */
    public String getSfen() {
        return SfenConverter.from(
                board,
                currentTurn,
                capturedPieces,
                moveNumber
        );
    }

    public void switchTurn() {
        this.currentTurn = this.currentTurn.opponent();
    }

    public void finish(PlayerType winner, FinishReason finishReason) {
        this.status = GameStatus.FINISHED;
        this.winner = winner;
        this.finishReason = finishReason;
    }

    public void selectShadow(PlayerType playerType, Position position) {
        if (status != GameStatus.SELECTING_SHADOW) {
            throw new IllegalStateException("影武者選択中ではありません");
        }

        Piece piece = board.getPiece(position);

        if (piece == null) {
            throw new IllegalArgumentException("駒がないマスは影武者に選択できません");
        }

        if (piece.getOwner() != playerType) {
            throw new IllegalArgumentException("自分の駒だけ影武者に選択できます");
        }

        if (playerType == PlayerType.SENTE) {
            if (senteShadowPosition != null) {
                throw new IllegalArgumentException("先手の影武者はすでに選択済みです");
            }
            senteShadowPosition = position;
        } else {
            if (goteShadowPosition != null) {
                throw new IllegalArgumentException("後手の影武者はすでに選択済みです");
            }
            goteShadowPosition = position;
        }

        if (senteShadowPosition != null && goteShadowPosition != null) {
            status = GameStatus.PLAYING;
        }
    }

    private Position getShadowPosition(PlayerType playerType) {
        return playerType == PlayerType.SENTE
                ? senteShadowPosition
                : goteShadowPosition;
    }

    private boolean isShadowPosition(PlayerType playerType, Position position) {
        Position shadowPosition = getShadowPosition(playerType);

        return shadowPosition != null && shadowPosition.equals(position);
    }

    private void updateShadowPositionIfNeeded(PlayerType playerType, Position from, Position to) {
        if (!isShadowPosition(playerType, from)) {
            return;
        }

        if (playerType == PlayerType.SENTE) {
            senteShadowPosition = to;
        } else {
            goteShadowPosition = to;
        }
    }

    public void resign(PlayerType playerType) {
        if (status == GameStatus.FINISHED) {
            throw new IllegalStateException("すでに終了したゲームです");
        }

        PlayerType winner = playerType.opponent();

        finish(winner, FinishReason.RESIGN);
    }

    /**
     * userTokenから先手・後手を判定する。
     */
    public PlayerType resolvePlayerType(String userToken) {
        if (userToken == null || userToken.isBlank()) {
            throw new IllegalArgumentException("userTokenが必要です");
        }

        if (userToken.equals(senteUserToken)) {
            return PlayerType.SENTE;
        }

        if (userToken.equals(goteUserToken)) {
            return PlayerType.GOTE;
        }

        throw new IllegalArgumentException("この対局の参加者ではありません");
    }
}