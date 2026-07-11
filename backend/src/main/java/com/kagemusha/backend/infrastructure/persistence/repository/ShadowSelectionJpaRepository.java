package com.kagemusha.backend.infrastructure.persistence.repository;

import com.kagemusha.backend.infrastructure.persistence.entity.ShadowSelectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * {@link ShadowSelectionEntity} の Spring Data JPA リポジトリ。
 */
public interface ShadowSelectionJpaRepository extends JpaRepository<ShadowSelectionEntity, UUID> {

    List<ShadowSelectionEntity> findByGameId(UUID gameId);
}
