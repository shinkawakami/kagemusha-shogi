package com.kagemusha.backend.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * {@link GamePlayerEntity} の Spring Data JPA リポジトリ。
 */
public interface GamePlayerJpaRepository extends JpaRepository<GamePlayerEntity, UUID> {

    List<GamePlayerEntity> findByGameId(UUID gameId);
}
