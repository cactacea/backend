package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao.AccountGroupsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyJoined, AccountNotJoined}

@Singleton
class AccountGroupsValidator @Inject()(accountGroupsDAO: AccountGroupsDAO) {

  def findByGroupId(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    accountGroupsDAO.findByGroupId(groupId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case _ =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def exist(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    accountGroupsDAO.exist(groupId, accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def notExist(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    accountGroupsDAO.exist(groupId, accountId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyJoined))
      case false =>
        Future.Unit
    })
  }

}

