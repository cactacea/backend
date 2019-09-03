package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.FriendsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyFriend, UserNotFriend}

@Singleton
class FriendsValidator @Inject()(friendsDAO: FriendsDAO) {

  def mustFriend(userId: UserId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(UserNotFriend))
    })
  }

  def mustNotFriend(userId: UserId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.own(userId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(UserAlreadyFriend))
      case false =>
        Future.Unit
    })
  }

}
