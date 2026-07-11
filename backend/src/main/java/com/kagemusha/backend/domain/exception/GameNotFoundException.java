package com.kagemusha.backend.domain.exception;

import java.util.UUID;

/**
 * 指定された対局が存在しないことを表す例外。
 *
 * <p>「見つからない」は「不正な入力（400）」とは区別され、HTTP 上は 404 に対応する。
 */
public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(UUID gameId) {
        super("対局が見つかりません: " + gameId);
    }
}
