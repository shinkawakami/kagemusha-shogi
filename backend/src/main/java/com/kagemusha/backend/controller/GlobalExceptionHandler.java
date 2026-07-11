package com.kagemusha.backend.controller;

import com.kagemusha.backend.domain.exception.GameNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 対局が存在しない場合は 404 Not Found を返す。
     */
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(GameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
    }

    /**
     * DB制約違反（主に game_moves の UNIQUE(game_id, ply) による着手競合）を
     * 409 Conflict として返す。楽観ロックの競合検出に対応する。
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "他の操作と競合しました。最新の状態を取得してやり直してください。"));
    }

    /**
     * 想定外の例外は 500 Internal Server Error として汎用メッセージを返す。
     *
     * <p>スタックトレースや内部メッセージはレスポンスに含めない（内部情報の露出防止）。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "予期しないエラーが発生しました。"));
    }
}
