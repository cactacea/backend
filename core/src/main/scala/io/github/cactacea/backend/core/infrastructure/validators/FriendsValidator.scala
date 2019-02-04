package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FriendsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyFriend, AccountNotFriend}

@Singleton
class FriendsValidator @Inject()(friendsDAO: FriendsDAO) {

  def notExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFriend))
      case false =>
        Future.Unit
    })
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(AccountNotFriend))
      case true =>
        Future.Unit
    })
  }

}
