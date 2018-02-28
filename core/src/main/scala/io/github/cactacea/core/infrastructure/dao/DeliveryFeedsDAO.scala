package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId}
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class DeliveryFeedsDAO @Inject()(db: DatabaseService) {

  import db._

  def findTokens(feedId: FeedId): Future[List[(AccountId, String, String)]] = {
    val q = quote {
      query[AccountFeeds].filter(af => af.feedId == lift(feedId) && af.notified == false)
        .filter(af => query[Relationships].filter(r => r.accountId == af.by && r.by == af.accountId && r.muted == true).isEmpty)
        .filter(af => query[PushNotificationSettings].filter(p => p.accountId == af.accountId && p.followerFeed == true).nonEmpty)
        .leftJoin(query[Relationships]).on((af, r) => r.accountId == af.by && r.by == af.accountId)
        .join(query[Feeds]).on({ case ((af, _), f) =>
          f.id == af.feedId &&
            ((f.privacyType == lift(FeedPrivacyType.everyone.toValue))
          || (f.privacyType == lift(FeedPrivacyType.followers.toValue) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == af.accountId).filter(_.followed == true)).nonEmpty))
          || (f.privacyType == lift(FeedPrivacyType.friends.toValue)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == af.accountId).filter(_.friend == true)).nonEmpty))
          || (f.by == af.accountId))})
        .join(query[Accounts]).on({ case (((af, _), _), a) =>  a.id == af.by})
        .join(query[Devices]).on({ case ((((af, _), _), _), d) => d.accountId == af.accountId && d.pushToken.isDefined})
        .map({case ((((af, r), _), a), d) => (a.displayName, r.map(_.editedDisplayName), af.accountId, d.pushToken) })
        .distinct
    }
    run(q).map(_.map({ case (displayName, editedAccountName, accountId, pushToken) => {
      val name = editedAccountName.flatMap(a => a).getOrElse(displayName)
      val token = pushToken.get
      (accountId, name, token)
    }}))

  }

}
