package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, CommentId}
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class DeliveryCommentsDAO @Inject()(db: DatabaseService) {

  import db._

  def findTokens(commentId: CommentId): Future[List[(AccountId, String, String)]] = {

    val q = quote {
      query[Comments].filter(c => c.id == lift(commentId) && c.notified == false)
        .join(query[Feeds]).on((c, f) => c.feedId == f.id
          && query[Relationships].filter(r => r.accountId == c.by && r.by == f.by && r.muted == true).isEmpty
          && query[PushNotificationSettings].filter(p => p.accountId == f.by && p.feedComment == true).nonEmpty)
        .leftJoin(query[Relationships]).on({ case ((c, f), r) => r.accountId == c.by && r.by == f.by})
        .join(query[Accounts]).on({ case (((c, _), _), a) =>  a.id == c.by})
        .join(query[Devices]).on({ case ((((_, f), _), _), d) => d.accountId == f.by && d.pushToken.isDefined})
        .map({case ((((_, f), r), a), d) => (a.displayName, r.map(_.editedDisplayName), f.by, d.pushToken) })
        .distinct
    }
    run(q).map(_.map({ case (displayName, editedAccountName, accountId, pushToken) => {
      val name = editedAccountName.flatMap(a => a).getOrElse(displayName)
      val token = pushToken.get
      (accountId, name, token)
    }}))

  }


}
