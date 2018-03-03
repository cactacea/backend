package io.github.cactacea.core.infrastructure.dao

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.results.PushNotifications
import io.github.cactacea.core.infrastructure.services.DatabaseService

class PushNotificationsDAO @Inject()(db: DatabaseService) {

  import db._

  def findFeeds(feedId: FeedId): Future[List[PushNotifications]] = {
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
      PushNotifications(accountId, name, token, false)
    }}))

  }

  def findGroupInvites(groupInviteId: GroupInviteId): Future[List[PushNotifications]] = {
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
      PushNotifications(accountId, name, token, false)
    }}))
  }

  def findMessages(messageId: MessageId): Future[List[PushNotifications]] = {
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
      PushNotifications(accountId, name, token, showMessage)
    }}))

  }

  def findComments(commentId: CommentId): Future[List[PushNotifications]] = {

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
      PushNotifications(accountId, name, token, false)
    }}))

  }

  //  def existFeeds(feedId: FeedId): Future[Option[Feeds]] = {
//    val q = quote {
//      query[Feeds]
//        .filter(_.id == lift(feedId))
//        .filter(_.notified == lift(false))
//    }
//    run(q).map(_.headOption)
//  }
//
//  def updateNotified(feedId: FeedId, notified: Boolean): Future[Boolean] = {
//    val q = quote {
//      query[Feeds]
//        .filter(_.id == lift(feedId))
//        .update(_.notified -> lift(notified))
//    }
//    run(q).map(_ == 1)
//  }

}
