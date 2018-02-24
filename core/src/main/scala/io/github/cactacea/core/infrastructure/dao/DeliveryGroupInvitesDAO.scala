package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupInviteId}
import io.github.cactacea.core.infrastructure.models._

@Singleton
class DeliveryGroupInvitesDAO @Inject()(db: DatabaseService) {

  import db._

  def findTokens(groupInviteId: GroupInviteId): Future[List[(AccountId, String, String)]] = {

    val q = quote {
      query[GroupInvites].filter(g => g.id == lift(groupInviteId) && g.notified == false
          && query[PushNotificationSettings].filter(p => p.accountId == g.accountId && p.groupInvite == true).nonEmpty)
        .leftJoin(query[Relationships]).on((g, r) => r.accountId == g.by && r.by == g.accountId)
        .join(query[Accounts]).on({ case ((g, _), a) => a.id == g.by})
        .join(query[Devices]).on({ case (((g, _), _), d) => d.accountId == g.accountId && d.pushToken.isDefined})
        .map({ case (((g, r), a), d) => (a.displayName, r.map(_.editedDisplayName), g.accountId, d.pushToken) })
        .distinct
    }

    run(q).map(_.map({ case (displayName, editedAccountName, accountId, pushToken) => {
      val name = editedAccountName.flatMap(a => a).getOrElse(displayName)
      val token = pushToken.get
      (accountId, name, token)
    }}))

  }


}
