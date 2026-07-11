package com.kagemusha.backend.port;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;

/**
 * 対局イベントの出力ポート（ヘキサゴナルアーキテクチャの outbound port）。
 *
 * <p>application 層（{@code GameService}）はこのインターフェースにのみ依存し、
 * WebSocket/STOMP などの配信技術には依存しない。実装（adapter）は
 * infrastructure/messaging 層に置く。
 */
public interface GameEventPort {

    /** 後手が参加したことを通知する。 */
    void publishPlayerJoined(Game game);

    /** 影武者が選択されたことを通知する。 */
    void publishShadowSelected(Game game, PlayerType selectedPlayer);

    /** 対局が開始したことを通知する。 */
    void publishGameStarted(Game game);

    /** 指し手が行われたことを通知する。 */
    void publishMove(Game game, String lastMove);

    /** 対局が終了したことを通知する。 */
    void publishGameFinished(Game game);
}
