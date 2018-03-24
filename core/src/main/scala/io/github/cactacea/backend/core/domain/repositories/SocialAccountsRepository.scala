package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.SocialAccount
import io.github.cactacea.backend.core.infrastructure.dao.SocialAccountsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

@Singleton
class SocialAccountsRepository {

  @Inject private var socialAccountsDAO: SocialAccountsDAO = _

  def findAll(sessionId: SessionId): Future[List[SocialAccount]] = {
    socialAccountsDAO.findAll(sessionId).map(_.map(t =>
      SocialAccount(t)
    ))
  }

  def create(socialAccountType: String, token: String, sessionId: SessionId): Future[Unit] = {
    socialAccountsDAO.exist(
      socialAccountType,
      sessionId
    ).flatMap(_ match {
      case false =>
        socialAccountsDAO.update(socialAccountType, true, sessionId).flatMap(_ match {
          case true =>
            Future.Unit
          case false =>
            socialAccountsDAO.create(socialAccountType, token, None, true, sessionId).flatMap(_ => Future.Unit)
        })
      case true =>
        Future.exception(CactaceaException(SocialAccountAlreadyConnected))
    })
  }

  def delete(socialAccountType: String, sessionId: SessionId): Future[Unit] = {
    socialAccountsDAO.delete(
      socialAccountType,
      sessionId
    ).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(SocialAccountNotConnected))
    })
  }

}
