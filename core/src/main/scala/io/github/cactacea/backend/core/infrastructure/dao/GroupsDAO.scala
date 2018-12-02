package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class GroupsDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(sessionId: SessionId): Future[GroupId] = {
    val organizedAt = timeService.currentTimeMillis()
    val name: Option[String] = None
    val by = sessionId.toAccountId
    val r = quote {
      query[Groups].insert(
        _.name              -> lift(name),
        _.invitationOnly    -> true,
        _.authorityType     -> lift(GroupAuthorityType.member),
        _.privacyType       -> lift(GroupPrivacyType.everyone),
        _.directMessage     -> true,
        _.accountCount      -> 0L,
        _.by                -> lift(by),
        _.organizedAt       -> lift(organizedAt)
      ).returning(_.id)
    }
    run(r)
  }


  def create(name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[GroupId] = {

    val organizedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val r = quote {
      query[Groups].insert(
        _.name                -> lift(name),
        _.invitationOnly      -> lift(byInvitationOnly),
        _.authorityType       -> lift(authority),
        _.privacyType         -> lift(privacyType),
        _.directMessage       -> false,
        _.accountCount        -> 0L,
        _.by                  -> lift(by),
        _.organizedAt         -> lift(organizedAt)
      ).returning(_.id)
    }
    run(r)
  }

  def delete(groupId: GroupId): Future[Unit] = {
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .delete
    }
    run(r).map(_ => Unit)
  }

  def update(groupId: GroupId,
             name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    val by = sessionId.toAccountId
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(_.by == lift(by))
        .update(
        _.name                -> lift(name),
        _.invitationOnly      -> lift(byInvitationOnly),
        _.privacyType         -> lift(privacyType),
        _.authorityType       -> lift(authority)
      )
    }
    run(r).map(_ => Unit)
  }

  def findAll(name: Option[String],
              invitationOnly: Option[Boolean],
              privacyType: Option[GroupPrivacyType],
              since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[Group]] = {

    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(g => g.directMessage == false)
        .filter(g => lift(since).forall(g.id < _))
        .filter(g => lift(name.map(_ + "%")).forall(n => g.name.exists(_ like n)))
        .filter(g => lift(invitationOnly).forall(g.invitationOnly ==  _))
        .filter(g => lift(privacyType).forall(g.privacyType == _))
        .filter(g => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == g.by) || (b.accountId == g.by && b.by == lift(by))
        ).isEmpty)
        .sortBy(_.id)(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map(g => Group(g, g.id.value)))
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
        .filter(g => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == g.by) || (b.accountId == g.by && b.by == lift(by))
        ).isEmpty)
    }
    run(q).map(_.headOption)
  }

  def exist(groupId: GroupId): Future[Boolean] = {
    val q = quote(
      query[Groups]
        .filter(_.id == lift(groupId))
        .nonEmpty
    )
    run(q)
  }

  def exist(groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .filter(g => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == g.by) || (b.accountId == g.by && b.by == lift(by))
        ).isEmpty)
        .nonEmpty
    }
    run(q)
  }

}

