package io.github.cactacea.backend.core.helpers.utils

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.helpers.tests.IntegrationFeatureTest
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

trait DAOHelper extends IntegrationFeatureTest {

  val db: DatabaseService

  import db._

  def existsUserTweets(tweetId: TweetId, userId: UserId): Boolean = {
    await(db.run(quote(query[UserTweets].filter(_.tweetId == lift(tweetId)).filter(_.userId == lift(userId)).nonEmpty)))
  }

  def findUserChannel(channelId: ChannelId, userId: UserId): Option[UserChannels] = {
    await(
      db.run(quote(query[UserChannels]
        .filter(_.channelId == lift(channelId))
        .filter(_.userId == lift(userId))
      )).map(_.headOption)
    )
  }

  def existsUserMessage(messageId: MessageId, userId: UserId): Boolean = {
    await(db.run(quote(query[UserMessages].filter(_.messageId == lift(messageId)).filter(_.userId == lift(userId)).nonEmpty)))
  }

  def findDevice(sessionId: SessionId): Future[Seq[Devices]] = {
    val userId = sessionId.userId
    val q = quote {
      query[Devices]
        .filter(_.userId == lift(userId))
        .sortBy(_.registeredAt)(Ord.desc)
    }
    db.run(q)
  }

  def findFriendRequest(id: FriendRequestId): Future[Option[FriendRequests]] = {
    db.run(quote(query[FriendRequests]
      .filter(_.id == lift(id))
    )).map(_.headOption)
  }

  def existsChannel(channelId: ChannelId): Future[Boolean] = {
    val q = quote(
      query[Channels]
        .filter(_.id == lift(channelId))
        .nonEmpty
    )
    db.run(q)
  }

  def findChannel(channelId: ChannelId): Future[Option[Channel]] = {
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
    }
    db.run(q).map(_.headOption.map(Channel(_)))
  }

  def findInvitation(channelId: ChannelId): Future[Seq[Invitations]] = {
    val q = quote {
      query[Invitations]
        .filter(_.channelId == lift(channelId))
    }
    db.run(q)
  }

  def findMessage(messageId: MessageId): Future[Option[Messages]] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
    }
    db.run(q).map(_.headOption)
  }

  def existsMessage(messageId: MessageId): Future[Boolean] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
        .nonEmpty
    }
    db.run(q)
  }

  def existsTweetMedium(tweetId: TweetId, mediumId: MediumId): Future[Boolean] = {
    val q = quote {
      query[TweetMediums]
        .filter(_.tweetId == lift(tweetId))
        .filter(_.mediumId == lift(mediumId))
        .nonEmpty
    }
    db.run(q)
  }

  def existsTweetTag(tweetId: TweetId, name: String): Future[Boolean] = {
    val q = quote {
      query[TweetTags]
        .filter(_.tweetId == lift(tweetId))
        .filter(_.name == lift(name))
        .nonEmpty
    }
    db.run(q)
  }

  def existsTweetReport(tweetId: TweetId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[TweetReports]
        .filter(_.tweetId == lift(tweetId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    db.run(q)
  }

  def existsCommentReport(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[CommentReports]
        .filter(_.commentId == lift(commentId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    db.run(q)
  }

  def findUserReport(userId: UserId, sessionId: SessionId): Future[Option[UserReports]] = {
    val by = sessionId.userId
    db.run(query[UserReports]
      .filter(_.userId == lift(userId))
      .filter(_.by == lift(by))
    ).map(_.headOption)
  }

  def findCommentReport(commentId: CommentId, sessionId: SessionId): Future[Option[CommentReports]] = {
    val by = sessionId.userId
    db.run(query[CommentReports]
      .filter(_.commentId == lift(commentId))
      .filter(_.by == lift(by))
    ).map(_.headOption)
  }

}
