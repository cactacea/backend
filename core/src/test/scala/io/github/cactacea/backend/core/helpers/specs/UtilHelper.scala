package io.github.cactacea.backend.core.helpers.specs

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.helpers.tests.IntegrationFeatureTest
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, CommentId, FeedId, FriendRequestId, MediumId, MessageId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.models.{Channels, CommentReports, Devices, FeedMediums, FeedReports, FeedTags, FriendRequests, Invitations, Messages, UserAuthentications, UserChannels, UserFeeds, UserMessages, UserReports}

trait UtilHelper extends IntegrationFeatureTest {

  val db: DatabaseService

  import db._

  def existsUserFeeds(feedId: FeedId, userId: UserId): Boolean = {
    await(db.run(quote(query[UserFeeds].filter(_.feedId == lift(feedId)).filter(_.userId == lift(userId)).nonEmpty)))
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

  def findDevice(sessionId: SessionId): Future[List[Devices]] = {
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

  def findInvitation(channelId: ChannelId): Future[List[Invitations]] = {
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

  def existsFeedMedium(feedId: FeedId, mediumId: MediumId): Future[Boolean] = {
    val q = quote {
      query[FeedMediums]
        .filter(_.feedId == lift(feedId))
        .filter(_.mediumId == lift(mediumId))
        .nonEmpty
    }
    db.run(q)
  }

  def existsFeedTag(feedId: FeedId, name: String): Future[Boolean] = {
    val q = quote {
      query[FeedTags]
        .filter(_.feedId == lift(feedId))
        .filter(_.name == lift(name))
        .nonEmpty
    }
    db.run(q)
  }

  def existsFeedReport(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[FeedReports]
        .filter(_.feedId == lift(feedId))
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

  def findUserAuthentication(providerId: String, providerKey: String): Future[Option[UserAuthentications]] = {
    val q = quote {
      query[UserAuthentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
    }
    db.run(q).map(_.headOption)
  }

}
