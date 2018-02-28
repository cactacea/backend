package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(sessionId: SessionId): Future[GroupId] = {
    for {
      id <- identifiesDAO.create().map(GroupId(_))
      _ <- insert(id, sessionId)
    } yield (id)
  }

  private def insert(id: GroupId, sessionId: SessionId): Future[Long] = {
    val organizedAt = System.nanoTime()
    val name: Option[String] = None
    val by = sessionId.toAccountId
    val r = quote {
      query[Groups].insert(
        _.id                -> lift(id),
        _.name              -> lift(name),
        _.byInvitationOnly  -> lift(true),
        _.authorityType     -> lift(GroupAuthorityType.member.toValue),
        _.privacyType       -> lift(GroupPrivacyType.everyone.toValue),
        _.isDirectMessage   -> lift(true),
        _.accountCount      -> lift(0L),
        _.by                -> lift(by),
        _.organizedAt       -> lift(organizedAt)
      )
    }
    run(r)
  }


  def create(name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, accountCount: Long, sessionId: SessionId): Future[GroupId] = {
    for {
      id <- identifiesDAO.create().map(GroupId(_))
      _ <- insert(id, name, byInvitationOnly, privacyType, authority, accountCount, sessionId)
    } yield (id)
  }

  private def insert(id: GroupId, name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, accountCount: Long, sessionId: SessionId): Future[Long] = {
    val organizedAt = System.nanoTime()
    val by = sessionId.toAccountId
    val r = quote {
      query[Groups].insert(
        _.id                  -> lift(id),
        _.name                -> lift(name),
        _.byInvitationOnly    -> lift(byInvitationOnly),
        _.authorityType       -> lift(authority.toValue),
        _.privacyType         -> lift(privacyType.toValue),
        _.isDirectMessage     -> lift(false),
        _.accountCount        -> lift(accountCount),
        _.by                  -> lift(by),
        _.organizedAt         -> lift(organizedAt)
      )
    }
    run(r)
  }

  def delete(groupId: GroupId): Future[Boolean] = {
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .delete
    }
    run(r).map(_ == 1)
  }

  def update(groupId: GroupId, name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(_.by == lift(by))
        .update(
        _.name                -> lift(name),
        _.byInvitationOnly    -> lift(byInvitationOnly),
        _.privacyType         -> lift(privacyType.toValue),
        _.authorityType       -> lift(authority.toValue)
      )
    }
    run(r).map(_ == 1)
  }

  def update(groupId: GroupId, messageId: Option[MessageId], sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(_.by == lift(by))
        .update(
          _.messageId -> lift(messageId)
        )
    }
    run(r).map(_ == 1)
  }

  def updateAccountCount(groupId: GroupId, count: Long): Future[Boolean] = {
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .update(g => g.accountCount -> (g.accountCount + lift(count)))
    }
    run(r).map(_ == 1)
  }

  def findAll(name: Option[String], byInvitationOnly: Option[Boolean], privacyType: Option[GroupPrivacyType], since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Groups]] = {
    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val n = name.fold("")(_ + "%")
    val b = byInvitationOnly.getOrElse(false)
    val i = byInvitationOnly.fold(0)(_ => 1)
    val p = privacyType.fold(-1L)(_.toValue)
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(g => g.by != lift(by))
        .filter(g => g.isDirectMessage == false)
        .filter(g => (g.name.forall(_ like lift(n)))  || lift(n) == "")
        .filter(g => g.byInvitationOnly == lift(b)    || lift(i) == 0)
        .filter(g => g.privacyType == lift(p)         || lift(p) == -1)
        .filter(g => query[Blocks]
          .filter(_.accountId    == g.by)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == lift(true) || b.beingBlocked == lift(true))
          .isEmpty)
        .filter(_.organizedAt < lift(s))
        .sortBy(_.organizedAt)(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)
  }

  def find(groupId: GroupId): Future[Option[Groups]] = {
    val q = quote {
      for {
        g <- query[Groups]
          .filter(_.id == lift(groupId))
      } yield (g)
    }
    run(q).map(_.headOption)
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Option[Groups]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(g => query[Blocks]
          .filter(_.accountId    == g.by)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == lift(true) || b.beingBlocked == lift(true))
          .isEmpty)
    }
    run(q).map(_.headOption)
  }

  def exist(groupId: GroupId): Future[Boolean] = {
    val q = quote(
      query[Groups]
        .filter(_.id == lift(groupId))
        .take(1)
        .size
    )
    run(q).map(_ == 1)
  }


}

