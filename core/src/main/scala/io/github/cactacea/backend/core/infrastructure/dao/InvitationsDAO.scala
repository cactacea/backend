package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Invitation
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class InvitationsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[InvitationId] = {
    val invitedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Invitations]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.groupId           -> lift(groupId),
          _.notified          -> false,
          _.invitedAt         -> lift(invitedAt)
        ).returning(_.id)
    }
    run(q)
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Invitations]
        .filter(_.accountId == lift(accountId))
        .filter(_.groupId   == lift(groupId))
        .filter(_.by        == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(accountId: AccountId, groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[Invitations]
        .filter(_.accountId == lift(accountId))
        .filter(_.groupId   == lift(groupId))
        .nonEmpty
    }
    run(q)
  }

  def find(accountId: AccountId, invitationId: InvitationId): Future[Option[(GroupId, AccountId)]] = {
    val q = quote {
      query[Invitations]
        .filter(_.id        == lift(invitationId))
        .filter(_.accountId == lift(accountId))
        .map(f => (f.groupId, f.accountId))
    }
    run(q).map(_.headOption)
  }


  def find(since: Option[Long], offset: Int,
           count: Int, sessionId: SessionId): Future[List[Invitation]] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Invitations]
        .filter(gi => gi.accountId == lift(by))
        .filter(gi => lift(since).forall(gi.id < _))
        .join(query[Groups]).on((gi, g) => g.id == gi.groupId)
        .join(query[Accounts]).on({ case ((gi, _), a) => a.id == gi.by})
        .leftJoin(query[Relationships]).on({ case (((_, _), a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({case (((gi, g), a), r) => (gi, a, r, g)})
        .sortBy({ case (gi, _, _, _) => gi.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (gi, a, r, g) => Invitation(gi, a, r, g, gi.id.value)}))
  }

  def own(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Invitations]
        .filter(_.accountId == lift(accountId))
        .filter(_.groupId   == lift(groupId))
        .filter(_.by        == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def delete(id: InvitationId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Invitations]
        .filter(_.accountId == lift(by))
        .filter(_.id == lift(id))
        .delete
    }
    run(q).map(_ => ())

  }

}
