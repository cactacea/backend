package io.github.cactacea.backend.core.helpers.specs

import com.twitter.util.Future
import com.twitter.util.logging.Logging
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.helpers.generators.ModelsGenerator
import io.github.cactacea.backend.core.helpers.tests.IntegrationFeatureTest
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, CommentId, FeedId, FriendRequestId, GroupId, MediumId, MessageId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{AccountFeeds, AccountGroups, AccountMessages, AccountReports, CommentReports, Devices, FeedMediums, FeedReports, FeedTags, FriendRequests, Groups, Invitations, Messages}
import org.scalatest.BeforeAndAfter
import org.scalatest.prop.GeneratorDrivenPropertyChecks

trait SpecHelper extends IntegrationFeatureTest
  with GeneratorDrivenPropertyChecks
  with ModelsGenerator
  with BeforeAndAfter
  with Logging {


  val db: DatabaseService

  import db._

  def existsAccountFeeds(feedId: FeedId, accountId: AccountId): Boolean = {
    await(db.run(quote(query[AccountFeeds].filter(_.feedId == lift(feedId)).filter(_.accountId == lift(accountId)).nonEmpty)))
  }

  def findAccountGroup(groupId: GroupId, accountId: AccountId): Option[AccountGroups] = {
    await(
      db.run(quote(query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .filter(_.accountId == lift(accountId))
      )).map(_.headOption)
    )
  }

  def existsAccountMessage(messageId: MessageId, accountId: AccountId): Boolean = {
    await(db.run(quote(query[AccountMessages].filter(_.messageId == lift(messageId)).filter(_.accountId == lift(accountId)).nonEmpty)))
  }

  def findDevice(sessionId: SessionId): Future[List[Devices]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices]
        .filter(_.accountId == lift(accountId))
        .sortBy(_.registeredAt)(Ord.desc)
    }
    db.run(q)
  }

  def findFriendRequest(id: FriendRequestId): Future[Option[FriendRequests]] = {
    db.run(quote(query[FriendRequests]
      .filter(_.id == lift(id))
    )).map(_.headOption)
  }

  def existsGroup(groupId: GroupId): Future[Boolean] = {
    val q = quote(
      query[Groups]
        .filter(_.id == lift(groupId))
        .nonEmpty
    )
    db.run(q)
  }

  def findGroup(groupId: GroupId): Future[Option[Group]] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
    }
    db.run(q).map(_.headOption.map(Group(_)))
  }

  def findInvitation(groupId: GroupId): Future[List[Invitations]] = {
    val q = quote {
      query[Invitations]
        .filter(_.groupId == lift(groupId))
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
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedReports]
        .filter(_.feedId == lift(feedId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    db.run(q)
  }

  def existsCommentReport(commentId: CommentId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentReports]
        .filter(_.commentId == lift(commentId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    db.run(q)
  }

  def findAccountReport(accountId: AccountId, sessionId: SessionId): Future[Option[AccountReports]] = {
    val by = sessionId.toAccountId
    db.run(query[AccountReports]
      .filter(_.accountId == lift(accountId))
      .filter(_.by == lift(by))
    ).map(_.headOption)
  }

  def findCommentReport(commentId: CommentId, sessionId: SessionId): Future[Option[CommentReports]] = {
    val by = sessionId.toAccountId
    db.run(query[CommentReports]
      .filter(_.commentId == lift(commentId))
      .filter(_.by == lift(by))
    ).map(_.headOption)
  }

}
