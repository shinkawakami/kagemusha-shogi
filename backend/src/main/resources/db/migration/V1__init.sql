-- Kagemusha Shogi 初期スキーマ
--
-- 設計方針は docs/db-architecture.md を参照。
-- 真実のデータは指し手ログ（game_moves）。現在局面（games.current_sfen/ply）は
-- そこから維持される投影（高速読み取り用キャッシュ）。
-- enum は移植性のため native ENUM ではなく VARCHAR + CHECK で表現する。

-- 対局の同一性とライフサイクル（1 行 / 対局）
CREATE TABLE games (
    id            UUID         PRIMARY KEY,
    mode          VARCHAR(16)  NOT NULL,
    status        VARCHAR(32)  NOT NULL,
    current_sfen  TEXT         NOT NULL,           -- 投影：現在局面
    ply           INTEGER      NOT NULL,           -- 現在手数 = 楽観ロックのバージョン兼用
    winner        VARCHAR(8),
    finish_reason VARCHAR(32),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    finished_at   TIMESTAMPTZ,
    CONSTRAINT chk_games_mode          CHECK (mode IN ('OFFLINE', 'ONLINE')),
    CONSTRAINT chk_games_status        CHECK (status IN ('WAITING', 'SELECTING_SHADOW', 'PLAYING', 'FINISHED')),
    CONSTRAINT chk_games_winner        CHECK (winner IS NULL OR winner IN ('SENTE', 'GOTE')),
    CONSTRAINT chk_games_finish_reason CHECK (finish_reason IS NULL OR finish_reason IN ('SHADOW_CAPTURED', 'RESIGN'))
);

-- プレイヤー割当（先手 / 後手）
-- user_token はオフラインでは NULL、オンラインでも後手参加前は NULL。
-- 将来は本物の users テーブルへ差し替え可能な seam。
CREATE TABLE game_players (
    id          UUID         PRIMARY KEY,
    game_id     UUID         NOT NULL REFERENCES games (id) ON DELETE CASCADE,
    player_type VARCHAR(8)   NOT NULL,
    user_token  TEXT,
    joined_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_game_players_type CHECK (player_type IN ('SENTE', 'GOTE')),
    CONSTRAINT uq_game_players        UNIQUE (game_id, player_type)
);

-- user_token から先手・後手を引く経路（resolvePlayerType 相当）用
CREATE INDEX idx_game_players_user_token ON game_players (user_token);

-- 指し手ログ（追記オンリー）。これが棋譜そのもの = 正データ。
-- UNIQUE(game_id, ply) で同一手数への二重着手を DB 制約で弾く（楽観ロック）。
CREATE TABLE game_moves (
    id         UUID         PRIMARY KEY,
    game_id    UUID         NOT NULL REFERENCES games (id) ON DELETE CASCADE,
    ply        INTEGER      NOT NULL,
    move_sfen  TEXT         NOT NULL,
    played_by  VARCHAR(8)   NOT NULL,
    played_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_game_moves_player CHECK (played_by IN ('SENTE', 'GOTE')),
    CONSTRAINT uq_game_moves          UNIQUE (game_id, ply)
);

-- 影武者（秘匿情報）。先手 / 後手で行を分け、相手の行を読まないことを徹底する。
CREATE TABLE shadow_selections (
    id          UUID         PRIMARY KEY,
    game_id     UUID         NOT NULL REFERENCES games (id) ON DELETE CASCADE,
    player_type VARCHAR(8)   NOT NULL,
    position    VARCHAR(4)   NOT NULL,
    selected_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_shadow_selections_type CHECK (player_type IN ('SENTE', 'GOTE')),
    CONSTRAINT uq_shadow_selections        UNIQUE (game_id, player_type)
);
