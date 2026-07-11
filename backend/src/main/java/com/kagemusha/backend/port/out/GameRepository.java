package com.kagemusha.backend.port.out;

import com.kagemusha.backend.domain.Game;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 対局アグリゲートの永続化ポート（ヘキサゴナルアーキテクチャの outbound port）。
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

    /**
     * 放置された対局を削除する。
     *
     * <p>「放置」とは、<strong>終局していない（{@code status != FINISHED}）</strong>かつ
     * {@code updatedBefore} より前から更新のない対局を指す。相手待ちのまま参加されない対局や、
     * 途中で中断された対局が対象。{@code updated_at} は着手・参加などの操作ごとに更新されるため、
     * 進行中の対局は削除されない。
     *
     * <p>終局済みの対局は棋譜・戦績として保持するため削除しない。
     * 子行（{@code game_players} / {@code game_moves} / {@code shadow_selections}）は
     * {@code ON DELETE CASCADE} により連鎖削除される。
     *
     * @param updatedBefore この時刻より {@code updated_at} が古い対局を対象にする
     * @return 削除した対局数
     */
    int deleteAbandoned(OffsetDateTime updatedBefore);
}
