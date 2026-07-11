package com.kagemusha.backend.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * {@link GameEntity} の Spring Data JPA リポジトリ。
 */
public interface GameJpaRepository extends JpaRepository<GameEntity, UUID> {
}
