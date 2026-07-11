package com.kagemusha.backend.infrastructure.persistence;

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
 * 影武者選択（秘匿情報）を表す永続化エンティティ。
 *
 * <p>{@code shadow_selections} テーブルに対応する。先手 / 後手で行を分け、
 * {@code position} は SFEN/USI 形式の座標文字列（例 "7g"）。
 * 影武者の駒が動くと {@code position} は更新される。
 */
@Entity
@Table(name = "shadow_selections")
@Getter
@Setter
@NoArgsConstructor
public class ShadowSelectionEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_type", nullable = false, length = 8)
    private PlayerType playerType;

    @Column(name = "position", nullable = false, length = 4)
    private String position;

    @Column(name = "selected_at", nullable = false)
    private OffsetDateTime selectedAt;
}
