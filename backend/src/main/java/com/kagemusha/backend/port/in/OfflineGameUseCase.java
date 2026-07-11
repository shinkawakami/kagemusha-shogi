package com.kagemusha.backend.port.in;

import com.kagemusha.backend.domain.Game;
import com.kagemusha.backend.domain.PlayerType;

import java.util.UUID;

/**
 * オフライン対局のユースケース（ヘキサゴナルアーキテクチャの inbound port）。
 *
 * <p>controller 層（inbound adapter）はこのインターフェースにのみ依存し、
 * application 層の具象（{@code GameService}）には直接依存しない。
 *
 * <p>オフラインは1つのブラウザで先手・後手が交互に操作するため、
 * userToken を持たず、操作する側を {@link PlayerType} で明示的に受け取る。
 */
public interface OfflineGameUseCase {

    /** オフライン対局を作成する。 */
    Game createOfflineGame();

    /** 対局を取得する。 */
    Game getGame(UUID gameId);

    /** オフライン対局で影武者を選択する。 */
    Game selectShadowOffline(UUID gameId, PlayerType playerType, String positionText);

    /** オフライン対局で指し手を実行する。 */
    Game moveOffline(UUID gameId, String moveText);

    /** オフライン対局で投了する。 */
    Game resignOffline(UUID gameId, PlayerType playerType);
}
