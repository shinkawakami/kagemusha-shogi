package com.kagemusha.backend.controller;

import com.kagemusha.backend.domain.exception.GameNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

/**
 * 例外を HTTP ステータスへマッピングする。
 *
 * <p>{@link ResponseEntityExceptionHandler} を継承し、ヘッダ欠落や不正な
 * リクエストボディなど Spring MVC 標準の例外は基底クラスに委ねて正しい 4xx を返す。
 * ドメイン由来の例外と、それ以外の想定外例外のみここで扱う。
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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
     * 上記いずれにも該当しない想定外の例外は 500 Internal Server Error として
     * 汎用メッセージを返す（スタックトレースや内部メッセージは露出しない）。
     *
     * <p>ヘッダ欠落・不正なボディなど Spring MVC 標準の例外は基底クラスが
     * より具体的なハンドラで先に処理するため、ここには到達しない。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "予期しないエラーが発生しました。"));
    }
}
