package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{CardId, CardExchangeId, MemberId}

case class CardExchanges(
                          id: CardExchangeId,
                          memberId: MemberId,
                          cardId: CardId,
                          exchangedAt: Long
                     )
