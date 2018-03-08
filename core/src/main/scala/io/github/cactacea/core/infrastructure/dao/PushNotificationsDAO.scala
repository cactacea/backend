package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.results.PushNotifications

@Singleton
class PushNotificationsDAO @Inject()(db: DatabaseService) {

  import db._

  def findByFeed(id: FeedId): Future[List[PushNotifications]] = {
    val q = quote {
      query[AccountFeeds].filter(af => af.feedId == lift(id) && af.notified == false)
        .filter(af => query[Relationships].filter(r => r.accountId == af.by && r.by == af.accountId && r.muted == true).isEmpty)
        .filter(af => query[PushNotificationSettings].filter(p => p.accountId == af.accountId && p.followerFeed == true).nonEmpty)
        .leftJoin(query[Relationships]).on((af, r) => r.accountId == af.by && r.by == af.accountId)
        .join(query[Feeds]).on({ case ((af, _), f) =>
        f.id == af.feedId &&
          ((f.privacyType == lift(FeedPrivacyType.everyone))
            || (f.privacyType == lift(FeedPrivacyType.followers) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == af.accountId).filter(_.followed == true)).nonEmpty))
            || (f.privacyType == lift(FeedPrivacyType.friends)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == af.accountId).filter(_.friend == true)).nonEmpty))
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

  def findByInvitationId(id: GroupInvitationId): Future[List[PushNotifications]] = {
    val q = quote {
      query[GroupInvitations].filter(g => g.id == lift(id) && g.notified == false
        && query[PushNotificationSettings].filter(p => p.accountId == g.accountId && p.groupInvitation == true).nonEmpty)
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

  def findByFriendRequestId(id: FriendRequestId): Future[List[PushNotifications]] = {
    val q = quote {
      query[FriendRequests].filter(g => g.id == lift(id) && g.notified == false
        && query[PushNotificationSettings].filter(p => p.accountId == g.accountId && p.groupInvitation == true).nonEmpty)
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

  def findByMessageId(id: MessageId): Future[List[PushNotifications]] = {
    val q = quote {
      query[AccountMessages]
        .filter(am => am.messageId == lift(id) && am.notified == false)
        .filter(am => query[Relationships].filter(r => r.accountId == am.by && r.by == am.accountId && r.muted == true).isEmpty)
        .join(query[Groups]).on((am, g) => g.id == am.groupId)
        .join(query[PushNotificationSettings]).on({ case ((am, g), p) => p.accountId == am.accountId &&
        ((p.directMessage == true && g.directMessage == true) || (p.groupMessage == true && g.directMessage == false))})
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

  def findByCommentId(id: CommentId): Future[List[PushNotifications]] = {

    val q = quote {
      query[Comments].filter(c => c.id == lift(id) && c.notified == false)
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

}
