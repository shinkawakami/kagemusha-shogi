package com.kagemusha.backend.infrastructure.persistence.entity;

import com.kagemusha.backend.domain.PlayerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 追記オンリーの指し手ログ（棋譜）の1エントリを表す永続化エンティティ。
 *
 * <p>{@code game_moves} テーブルに対応する。これが対局の正データであり、
 * {@code UNIQUE(game_id, ply)} により同一手数への二重着手を弾く。
 */
@Entity
@Table(name = "game_moves")
@Getter
@Setter
@NoArgsConstructor
public class GameMoveEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "ply", nullable = false)
    private int ply;

    @Column(name = "move_sfen", nullable = false, columnDefinition = "text")
    private String moveSfen;

    @Enumerated(EnumType.STRING)
    @Column(name = "played_by", nullable = false, length = 8)
    private PlayerType playedBy;

    @Column(name = "played_at", nullable = false)
    private OffsetDateTime playedAt;
}
