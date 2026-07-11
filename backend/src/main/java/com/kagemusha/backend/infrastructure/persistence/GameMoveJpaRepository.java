package com.kagemusha.backend.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * {@link GameMoveEntity} の Spring Data JPA リポジトリ。
 */
public interface GameMoveJpaRepository extends JpaRepository<GameMoveEntity, UUID> {

    List<GameMoveEntity> findByGameIdOrderByPlyAsc(UUID gameId);
}
