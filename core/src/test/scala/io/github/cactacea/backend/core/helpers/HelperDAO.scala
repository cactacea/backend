package io.github.cactacea.backend.core.helpers

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{FriendRequestId, GroupId, GroupInvitationId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, FriendRequests, GroupInvitations, Groups}

@Singleton
class HelperDAO @Inject()(db: DatabaseService) {

  import db._

  def selectGroupInvitation(id: GroupInvitationId, sessionId: SessionId): Future[Option[GroupInvitations]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[GroupInvitations]
        .filter(_.id        == lift(id))
        .filter(_.accountId == lift(accountId))
    }
    run(q).map(_.headOption)
  }

  def selectGroup(groupId: GroupId): Future[Option[Groups]] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
    }
    run(q).map(_.headOption)
  }

  def selectFriendRequest(id: FriendRequestId, sessionId: SessionId): Future[Option[FriendRequests]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
        .filter(_.accountId   == lift(accountId))
    }
    run(q).map(_.headOption)
  }

  def selectAccount(sessionId: SessionId): Future[Option[Accounts]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
    }
    run(q).map(_.headOption)
  }

}
