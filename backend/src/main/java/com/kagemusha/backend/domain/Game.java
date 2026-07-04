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
    private PlayerType winner;
    private Position senteShadowPosition;
    private Position goteShadowPosition;

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
        this.status = GameStatus.PLAYING;
        this.winner = null;
    }

    /**
     * 初期状態のゲームを作成する
     */
    public static Game createInitialGame(Long id) {
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
     * 駒を移動する
     */
    public void move(String moveText) {
        if (status == GameStatus.FINISHED) {
            throw new IllegalStateException("すでに終了したゲームです");
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
            finish(movingPiece.getOwner());
            moveNumber++;
            return;
        }

        switchTurn();
        moveNumber++;
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

    public PlayerType getWinner() {
        return winner;
    }

    /**
     * 盤面部分だけのSFENを返す。
     *
     * 例:
     * lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL
     */
    public String getBoardSfen() {
        return SfenConverter.fromBoardOnly(board);
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
        this.currentTurn = this.currentTurn.opposite();
    }

    public void finish(PlayerType winner) {
        this.status = GameStatus.FINISHED;
        this.winner = winner;
    }

    public void selectShadow(PlayerType playerType, Position position) {
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

    public Position getShadowPosition(PlayerType playerType) {
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

        PlayerType winner = playerType.opposite();

        finish(winner);
    }
}