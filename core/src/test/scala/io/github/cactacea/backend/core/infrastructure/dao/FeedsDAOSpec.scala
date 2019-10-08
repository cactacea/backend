package io.github.cactacea.backend.core.infrastructure.dao

import java.util.Locale

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.{TweetPrivacyType, FeedType}
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Feeds

class FeedsDAOSpec extends DAOSpec {

  import db._

  feature("createInvitation") {
    scenario("should create a invitation notification") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsDAO.create(userId, channelId, sessionId))
        val notificationId = await(notificationsDAO.create(invitationId, userId, sessionId))
        val link = deepLinkService.getInvitation(invitationId)

        // result
        val result = await(db.run(query[Feeds].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.userId == userId))
        assert(result.exists(_.by == sessionId.userId))
        assert(result.exists(_.feedType == FeedType.invitation))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should create a friend request notification") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a1.userName))
        val friendRequestId = await(friendRequestsDAO.create(userId, sessionId))
        val notificationId = await(notificationsDAO.create(friendRequestId, userId, sessionId))
        val link = deepLinkService.getRequest(friendRequestId)

        // result
        val result = await(db.run(query[Feeds].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.userId == userId))
        assert(result.exists(_.by == sessionId.userId))
        assert(result.exists(_.feedType == FeedType.friendRequest))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    // TODO : Follower test

    scenario("should create a tweet notification if user is a friend") {
      forOne(userGen, userGen, tweetGen) { (s, a1, f) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a1.userName))
        await(friendsDAO.create(userId, sessionId))
        await(friendsDAO.create(sessionId.userId, userId.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(notificationsDAO.create(tweetId, sessionId))
        val link = deepLinkService.getTweet(tweetId)
        val contentId = tweetId.value

        // result
        val result = await(db.run(query[Feeds].filter(_.by == lift(sessionId.userId)).filter(_.contentId == lift(contentId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.userId == userId))
        assert(result.exists(_.by == sessionId.userId))
        assert(result.exists(_.feedType == FeedType.tweet))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should create a tweet reply notification") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a1, f, c) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a1.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, userId.sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))
        val notificationId = await(notificationsDAO.create(tweetId, commentId, userId, false, sessionId))
        val link = deepLinkService.getComment(tweetId, commentId)

        // result
        val result = await(db.run(query[Feeds].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.userId == userId))
        assert(result.exists(_.by == sessionId.userId))
        assert(result.exists(_.feedType == FeedType.tweetReply))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should create a comment reply notification") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a1, f, c) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a1.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, userId.sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))
        val notificationId = await(notificationsDAO.create(tweetId, commentId, userId, true, sessionId))
        val link = deepLinkService.getComment(tweetId, commentId)

        // result
        val result = await(db.run(query[Feeds].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(_.id == notificationId))
        assert(result.exists(_.userId == userId))
        assert(result.exists(_.by == sessionId.userId))
        assert(result.exists(_.feedType == FeedType.commentReply))
        assert(result.exists(_.url == link))
        assert(result.exists(_.unread))
      }
    }

    scenario("should return an exception occurs if duplicated") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsDAO.create(userId, channelId, sessionId))
        await(notificationsDAO.create(invitationId, userId, sessionId))

        // exception occurs
        assert(intercept[ServerError] {
          await(notificationsDAO.create(invitationId, userId, sessionId))
        }.code == 1062)
      }
    }

  }


  feature("updateReadStatus") {
    scenario("should update read status") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsDAO.create(userId, channelId, sessionId))
        val notificationId = await(notificationsDAO.create(invitationId, userId, sessionId))
        await(notificationsDAO.updateReadStatus(Seq(notificationId), userId.sessionId))

        // result
        val result = await(db.run(query[Feeds].filter(_.id == lift(notificationId)))).headOption
        assert(result.isDefined)
        assert(result.exists(!_.unread))
      }
    }
  }

  feature("find") {
    scenario("should return notification user received") {
      forOne(userGen, userGen, channel20SeqGen) { (s, a, l) =>

        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val notificationIds = l.map({ g =>
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val invitationId = await(invitationsDAO.create(userId, channelId, sessionId))
          await(notificationsDAO.create(invitationId, userId, sessionId))
        }).reverse

        // result
        val result1 = await(notificationsDAO.find(None, 0, 10, Seq(Locale.getDefault()), userId.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.foreach({ case (c, i) =>
          assert(c.id == notificationIds(i))
        })

        val result2 = await(notificationsDAO.find(result1.lastOption.map(_.next), 0, 10, Seq(Locale.getDefault()), userId.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.foreach({ case (c, i) =>
          assert(c.id == notificationIds(i + result1.size))
        })

        val result3 = await(notificationsDAO.find(result2.lastOption.map(_.next), 0, 10, Seq(Locale.getDefault()), userId.sessionId))
        assert(result3.size == 0)

      }
    }
  }

}
