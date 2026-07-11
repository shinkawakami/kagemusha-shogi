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
 * 対局のプレイヤー割当（先手 / 後手）を表す永続化エンティティ。
 *
 * <p>{@code game_players} テーブルに対応する。オンラインでは
 * {@code userToken} でプレイヤーを識別する。オフラインは token を持たない。
 */
@Entity
@Table(name = "game_players")
@Getter
@Setter
@NoArgsConstructor
public class GamePlayerEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_type", nullable = false, length = 8)
    private PlayerType playerType;

    @Column(name = "user_token", columnDefinition = "text")
    private String userToken;

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt;
}
