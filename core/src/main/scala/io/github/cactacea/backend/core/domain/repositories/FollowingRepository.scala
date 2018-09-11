package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.GroupPrivacyType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.{FollowersDAO, FollowingDAO, GroupInvitationsDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowingRepository {

  @Inject private var followingDAO: FollowingDAO = _
  @Inject private var followersDAO: FollowersDAO = _
  @Inject private var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    for {
      _ <- validationDAO.existAccount(accountId)
      r <- followingDAO.findAll(accountId, since, offset, count, sessionId)
        .map(_.map( t => Account(t._1, t._2, t._3.followedAt)))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followingDAO.findAll(since, offset, count, sessionId)
      .map(_.map( t => Account(t._1, t._2, t._3.followedAt)))
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.notExistFollow(accountId, sessionId)
      _ <- followingDAO.create(accountId, sessionId)
      _ <- followersDAO.create(sessionId.toAccountId, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existFollow(accountId, sessionId)
      _ <- followingDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.following, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.followers, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

}
