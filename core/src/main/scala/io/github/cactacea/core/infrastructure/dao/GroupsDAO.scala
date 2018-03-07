package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class GroupsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def create(sessionId: SessionId): Future[GroupId] = {
    for {
      id <- identifyService.generate().map(GroupId(_))
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
        _.invitationOnly  -> true,
        _.authorityType     -> lift(GroupAuthorityType.member),
        _.privacyType       -> lift(GroupPrivacyType.everyone),
        _.directMessage   -> true,
        _.accountCount      -> 0L,
        _.by                -> lift(by),
        _.organizedAt       -> lift(organizedAt)
      )
    }
    run(r)
  }


  def create(name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, accountCount: Long, sessionId: SessionId): Future[GroupId] = {
    for {
      id <- identifyService.generate().map(GroupId(_))
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
        _.invitationOnly    -> lift(byInvitationOnly),
        _.authorityType       -> lift(authority),
        _.privacyType         -> lift(privacyType),
        _.directMessage     -> false,
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
        _.invitationOnly    -> lift(byInvitationOnly),
        _.privacyType         -> lift(privacyType),
        _.authorityType       -> lift(authority)
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

  def findAll(name: Option[String], invitationOnly: Option[Boolean], privacyType: Option[GroupPrivacyType], since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Groups]] = {
    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val n = name.fold("")(_ + "%")
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(g => g.by != lift(by))
        .filter(g => g.directMessage == false)
        .filter(g => (g.name.forall(_ like lift(n)))  || lift(name).isEmpty)
        .filter(g => g.invitationOnly == lift(invitationOnly.getOrElse(false))    || lift(invitationOnly).isEmpty)
        .filter(g => g.privacyType == lift(privacyType.getOrElse(GroupPrivacyType.everyone))         || lift(privacyType).isEmpty)
        .filter(g => query[Blocks]
          .filter(_.accountId    == g.by)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == true || b.beingBlocked == true)
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
          .filter(b => b.blocked == true || b.beingBlocked == true)
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

