package io.github.cactacea.backend.auth.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class AuthenticationsValidator @Inject()(
                                          authenticationsDAO: AuthenticationsDAO
                           ) {

//  def mustFind(providerId: String, providerKey: String): Future[Authentication] = {
//    authenticationsDAO.find(providerId, providerKey).flatMap(_ match {
//      case Some(a) =>
//        Future.value(a)
////        a.userId match {
////          case Some(id) =>
////            Future.value(id)
////          case None =>
////            Future.exception(CactaceaException(UserNotFound))
////        }
//      case None =>
//        Future.exception(CactaceaException(UserNotFound))
//    })
//  }

  def mustNotExists(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsDAO.exists(providerId, providerKey).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(UserAlreadyExist))
    })
  }

  def mustExists(providerId: String, providerKey: String): Future[Unit] = {
    authenticationsDAO.exists(providerId, providerKey).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(UserNotFound))
      case true =>
        Future.Unit
    })
  }

}


