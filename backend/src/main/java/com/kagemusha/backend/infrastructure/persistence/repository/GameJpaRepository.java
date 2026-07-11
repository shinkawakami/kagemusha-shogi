package com.kagemusha.backend.infrastructure.persistence.repository;

import com.kagemusha.backend.domain.GameStatus;
import com.kagemusha.backend.infrastructure.persistence.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * {@link GameEntity} の Spring Data JPA リポジトリ。
 */
public interface GameJpaRepository extends JpaRepository<GameEntity, UUID> {

    /**
     * 指定ステータス以外で、指定時刻より前から更新のない対局を一括削除する。
     *
     * <p>子行はテーブル定義の {@code ON DELETE CASCADE} により DB 側で連鎖削除される。
     *
     * @param keepStatus    削除対象から除外するステータス（終局済みを保持するため {@code FINISHED} を渡す）
     * @param updatedBefore この時刻より {@code updated_at} が古い対局を対象にする
     * @return 削除した対局数
     */
    @Modifying
    @Query("DELETE FROM GameEntity g WHERE g.status <> :keepStatus AND g.updatedAt < :updatedBefore")
    int deleteByStatusNotAndUpdatedAtBefore(
            @Param("keepStatus") GameStatus keepStatus,
            @Param("updatedBefore") OffsetDateTime updatedBefore
    );
}
