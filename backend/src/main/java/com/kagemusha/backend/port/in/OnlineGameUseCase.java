package com.kagemusha.backend.port.in;

import com.kagemusha.backend.domain.Game;

import java.util.UUID;

/**
 * オンライン対局のユースケース（ヘキサゴナルアーキテクチャの inbound port）。
 *
 * <p>controller 層（inbound adapter）はこのインターフェースにのみ依存し、
 * application 層の具象（{@code GameService}）には直接依存しない。
 *
 * <p>オンラインは userToken でプレイヤーを識別し、先手・後手の判定や
 * 手番チェックはユースケース側で行う。
 */
public interface OnlineGameUseCase {

    /** オンライン対局を作成する（作成者は先手）。 */
    Game createOnlineGame(String userToken);

    /** オンライン対局に参加する（参加者は後手）。 */
    Game joinOnlineGame(UUID gameId, String userToken);

    /** 対局を取得する。 */
    Game getGame(UUID gameId);

    /** オンライン対局で影武者を選択する。 */
    Game selectShadowOnline(UUID gameId, String userToken, String positionText);

    /** オンライン対局で指し手を実行する。 */
    Game moveOnline(UUID gameId, String userToken, String moveText);

    /** オンライン対局で投了する。 */
    Game resignOnline(UUID gameId, String userToken);
}
