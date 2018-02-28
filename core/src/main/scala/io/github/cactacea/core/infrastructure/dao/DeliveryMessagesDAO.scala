package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MessageId}
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class DeliveryMessagesDAO @Inject()(db: DatabaseService) {

  import db._

  def findTokens(messageId: MessageId): Future[List[(AccountId, String, String, Boolean)]] = {
    val q = quote {
      query[AccountMessages]
        .filter(am => am.messageId == lift(messageId) && am.notified == false)
        .filter(am => query[Relationships].filter(r => r.accountId == am.by && r.by == am.accountId && r.muted == true).isEmpty)
        .join(query[Groups]).on((am, g) => g.id == am.groupId)
        .join(query[PushNotificationSettings]).on({ case ((am, g), p) => p.accountId == am.accountId &&
          ((p.directMessage == true && g.isDirectMessage == true) || (p.groupMessage == true && g.isDirectMessage == false))})
        .leftJoin(query[Relationships]).on({ case (((am, g), _), r) =>  r.accountId == am.by && r.by == am.accountId })
        .join(query[Accounts]).on({ case ((((am, _), _), _), a) =>  a.id == am.by})
        .join(query[Devices]).on({ case (((((am, _), _), _), _), d) => d.accountId == am.accountId && d.pushToken.isDefined})
        .map({case (((((am, _), p), r), a), d) => (a.displayName, r.map(_.editedDisplayName), p.showMessage, am.accountId, d.pushToken) })
        .distinct
    }
    run(q).map(_.map({ case (displayName, editedAccountName, showMessage, accountId, pushToken) => {
      val name = editedAccountName.flatMap(a => a).getOrElse(displayName)
      val token = pushToken.get
      (accountId, name, token, showMessage)
    }}))

  }

}
