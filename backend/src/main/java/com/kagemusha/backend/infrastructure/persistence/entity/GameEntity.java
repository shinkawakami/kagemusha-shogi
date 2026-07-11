package com.kagemusha.backend.infrastructure.persistence.entity;

import com.kagemusha.backend.domain.FinishReason;
import com.kagemusha.backend.domain.GameMode;
import com.kagemusha.backend.domain.GameStatus;
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
 * 対局の同一性とライフサイクル、および現在局面の投影を保持する永続化エンティティ。
 *
 * <p>{@code games} テーブルに対応する。{@code currentSfen} / {@code ply} は
 * 指し手ログ（{@link GameMoveEntity}）から維持される投影（キャッシュ）。
 */
@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class GameEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 16)
    private GameMode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private GameStatus status;

    @Column(name = "current_sfen", nullable = false, columnDefinition = "text")
    private String currentSfen;

    @Column(name = "ply", nullable = false)
    private int ply;

    @Enumerated(EnumType.STRING)
    @Column(name = "winner", length = 8)
    private PlayerType winner;

    @Enumerated(EnumType.STRING)
    @Column(name = "finish_reason", length = 32)
    private FinishReason finishReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;
}
