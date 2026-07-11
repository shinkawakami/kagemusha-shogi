package com.kagemusha.backend.domain;

public enum GameStatus {
    WAITING,           // 相手待ち
    SELECTING_SHADOW,  // 影武者選択中
    PLAYING,           // 対局中
    FINISHED           // 終了
}