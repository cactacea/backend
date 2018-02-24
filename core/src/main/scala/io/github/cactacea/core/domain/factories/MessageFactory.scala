package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.infrastructure.models._

object MessageFactory {

  def create(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships]): Message = {
    create(m, am, i, a, r, None, None)
  }

  def create(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships], a2: Option[Accounts], r2: Option[Relationships]): Message = {
    val images = i.map(MediumFactory.create(_))
    val by = AccountFactory.create(a, r)
    val account = a2.map(AccountFactory.create(_, r2))
    Message(
      id                = m.id,
      messageType       = m.messageType,
      message           = m.message,
      medium            = images,
      by                = by,
      account           = account,
      unread            = am.unread,
      accountCount      = m.accountCount,
      readAccountCount  = m.readAccountCount,
      postedAt          = m.postedAt
    )
  }

}
