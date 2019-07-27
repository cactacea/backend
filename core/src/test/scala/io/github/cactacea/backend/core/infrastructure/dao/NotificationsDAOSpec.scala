package io.github.cactacea.backend.core.infrastructure.dao

import java.util.Locale

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, NotificationType}
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Notifications

class NotificationsDAOSpec extends DAOSpec {

  import db._

  feature("createInvitation") {
    scenario("should create a invitation notification") {
      forOne(accountGen, accountGen, groupGen) { (s, a, g) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsDAO.create(accountId, groupId, sessionId))
        val notificationId = await(notificationsDAO.create(invitationId, accountId, sessionId))
        val link = deepLinkService.getInvitation(invitationId)

        // result
        val result = await(db.run(query[Notifications].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.accountId == accountId))
        assert(result.exists(_.by == sessionId.toAccountId))
        assert(result.exists(_.notificationType == NotificationType.invitation))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should create a friend friendRequest notification") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a1.accountName))
        val friendRequestId = await(friendRequestsDAO.create(accountId, sessionId))
        val notificationId = await(notificationsDAO.create(friendRequestId, accountId, sessionId))
        val link = deepLinkService.getRequest(friendRequestId)

        // result
        val result = await(db.run(query[Notifications].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.accountId == accountId))
        assert(result.exists(_.by == sessionId.toAccountId))
        assert(result.exists(_.notificationType == NotificationType.friendRequest))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    // TODO : Follower test

    scenario("should create a feed notification if account is a friend") {
      forOne(accountGen, accountGen, feedGen) { (s, a1, f) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a1.accountName))
        await(friendsDAO.create(accountId, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(notificationsDAO.create(feedId, sessionId))
        val link = deepLinkService.getFeed(feedId)
        val contentId = feedId.value

        // result
        val result = await(db.run(query[Notifications].filter(_.by == lift(sessionId.toAccountId)).filter(_.contentId == lift(contentId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.accountId == accountId))
        assert(result.exists(_.by == sessionId.toAccountId))
        assert(result.exists(_.notificationType == NotificationType.feed))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should create a feed reply notification") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a1, f, c) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a1.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, accountId.toSessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))
        val notificationId = await(notificationsDAO.create(feedId, commentId, accountId, false, sessionId))
        val link = deepLinkService.getComment(feedId, commentId)

        // result
        val result = await(db.run(query[Notifications].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.accountId == accountId))
        assert(result.exists(_.by == sessionId.toAccountId))
        assert(result.exists(_.notificationType == NotificationType.feedReply))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should create a comment reply notification") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a1, f, c) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a1.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, accountId.toSessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))
        val notificationId = await(notificationsDAO.create(feedId, commentId, accountId, true, sessionId))
        val link = deepLinkService.getComment(feedId, commentId)

        // result
        val result = await(db.run(query[Notifications].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.accountId == accountId))
        assert(result.exists(_.by == sessionId.toAccountId))
        assert(result.exists(_.notificationType == NotificationType.commentReply))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should return an exception occurs if duplicated") {
      forOne(accountGen, accountGen, groupGen) { (s, a, g) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsDAO.create(accountId, groupId, sessionId))
        await(notificationsDAO.create(invitationId, accountId, sessionId))

        // exception occurs
        assert(intercept[ServerError] {
          await(notificationsDAO.create(invitationId, accountId, sessionId))
        }.code == 1062)
      }
    }

  }


  feature("updateReadStatus") {
    scenario("should update read status") {
      forOne(accountGen, accountGen, groupGen) { (s, a, g) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsDAO.create(accountId, groupId, sessionId))
        val notificationId = await(notificationsDAO.create(invitationId, accountId, sessionId))
        await(notificationsDAO.updateReadStatus(List(notificationId), accountId.toSessionId))

        // result
        val result = await(db.run(query[Notifications].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(!_.unread))
      }
    }
  }

  feature("find") {
    scenario("should return notification account received") {
      forOne(accountGen, accountGen, group20ListGen) { (s, a, l) =>

        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val notificationIds = l.map({ g =>
          val groupId = await(groupsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val invitationId = await(invitationsDAO.create(accountId, groupId, sessionId))
          await(notificationsDAO.create(invitationId, accountId, sessionId))
        }).reverse

        // result
        val result1 = await(notificationsDAO.find(None, 0, 10, Seq(Locale.getDefault()), accountId.toSessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.foreach({ case (c, i) =>
          assert(c.id == notificationIds(i))
        })

        val result2 = await(notificationsDAO.find(result1.lastOption.map(_.next), 0, 10, Seq(Locale.getDefault()), accountId.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.foreach({ case (c, i) =>
          assert(c.id == notificationIds(i + result1.size))
        })

        val result3 = await(notificationsDAO.find(result2.lastOption.map(_.next), 0, 10, Seq(Locale.getDefault()), accountId.toSessionId))
        assert(result3.size == 0)

      }
    }
  }

}
