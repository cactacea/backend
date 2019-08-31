package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FriendsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyFriend, AccountNotFriend}

@Singleton
class FriendsValidator @Inject()(friendsDAO: FriendsDAO) {

  def mustFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.own(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFriend))
    })
  }

  def mustNotFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.own(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFriend))
      case false =>
        Future.Unit
    })
  }

}
