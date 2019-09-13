package io.github.cactacea.backend.auth.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class AuthenticationsValidator @Inject()(authenticationsDAO: AuthenticationsDAO) {


  def mustFind(providerId: String, providerKey: String): Future[Authentication] = {
    authenticationsDAO.find(providerId, providerKey).flatMap(_ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(UserNotFound))
    })
  }

  def mustFindUserId(providerId: String, providerKey: String): Future[UserId] = {
    authenticationsDAO.find(providerId, providerKey).flatMap(_.flatMap(_.userId) match {
      case Some(id) =>
        Future.value(id)
      case None =>
        Future.exception(CactaceaException(UserNotFound))
    })
  }

  def mustFindProviderKey(providerId: String, userId: UserId): Future[String] = {
    authenticationsDAO.find(providerId, userId).flatMap(_ match {
      case Some(a) =>
        Future.value(a.providerKey)
      case None =>
        Future.exception(CactaceaException(AccountNotLinked))
    })
  }

  def mustNotExists(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsDAO.exists(providerId, providerKey).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(UserAlreadyExist))
    })
  }

//  def mustExists(providerId: String, userId: UserId): Future[Unit] = {
//    authenticationsDAO.exists(providerId, userId).flatMap(_ match {
//      case false =>
//        Future.exception(CactaceaException(AccountNotLinked))
//      case true =>
//        Future.Unit
//    })
//  }

  def mustNotExists(providerId: String, userId: UserId): Future[Unit] = {
    authenticationsDAO.exists(providerId, userId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountAlreadyLinked))
    })
  }

}


