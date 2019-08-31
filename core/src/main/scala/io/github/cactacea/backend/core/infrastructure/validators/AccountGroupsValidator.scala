package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao.AccountGroupsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyJoined, AccountNotJoined, GroupAlreadyHidden, GroupNotHidden}

@Singleton
class AccountGroupsValidator @Inject()(accountGroupsDAO: AccountGroupsDAO) {

  def mustFindByAccountId(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    accountGroupsDAO.findByAccountId(accountId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case _ =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def mustJoined(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    accountGroupsDAO.exists(groupId, accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def mustNotJoined(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    accountGroupsDAO.exists(groupId, accountId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyJoined))
      case false =>
        Future.Unit
    })
  }

  def mustHidden(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountGroupsDAO.isHidden(groupId, sessionId).flatMap(_ match {
      case Some(true) =>
        Future.Unit
      case Some(false) =>
        Future.exception(CactaceaException(GroupNotHidden))
      case None =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def mustNotHidden(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountGroupsDAO.isHidden(groupId, sessionId).flatMap(_ match {
      case Some(true) =>
        Future.exception(CactaceaException(GroupAlreadyHidden))
      case Some(false) =>
        Future.Unit
      case None =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

}

