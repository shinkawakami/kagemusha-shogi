package com.kagemusha.backend.port;

import com.kagemusha.backend.domain.Game;

import java.util.Optional;
import java.util.UUID;

/**
 * 対局アグリゲートの永続化ポート（ヘキサゴナルアーキテクチャの port）。
 *
 * <p>application 層（{@code GameService}）はこのインターフェースにのみ依存し、
 * JPA などの永続化技術には依存しない。実装（adapter）は
 * infrastructure/persistence 層に置く。
 *
 * <p>永続化モデルの詳細（指し手ログ {@code game_moves} を正とし、
 * 現在局面を投影として維持する）は実装側の関心事であり、
 * このポートには漏らさない。詳細は {@code docs/db-architecture.md} を参照。
 */
public interface GameRepository {

    /**
     * 対局を永続化する（新規作成・更新の両方）。
     *
     * <p>更新時、同一手数への二重着手など並行更新が競合した場合は
     * 楽観ロックにより失敗する。実装は {@code games.ply} と
     * {@code UNIQUE(game_id, ply)} 制約でこれを担保する。
     *
     * @param game 保存する対局アグリゲート
     * @return 保存後の対局
     */
    Game save(Game game);

    /**
     * ID で対局を取得する。
     *
     * @param id 対局ID
     * @return 対局。存在しなければ {@link Optional#empty()}。
     */
    Optional<Game> findById(UUID id);
}
