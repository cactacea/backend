package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.domain.factories.SocialAccountFactory
import io.github.cactacea.core.domain.models.SocialAccount
import io.github.cactacea.core.infrastructure.dao.SocialAccountsDAO
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

@Singleton
class SocialAccountsRepository {

  @Inject var socialAccountsDAO: SocialAccountsDAO = _

  def findAll(sessionId: SessionId): Future[List[SocialAccount]] = {
    socialAccountsDAO.findAll(sessionId).map(_.map(t =>
      SocialAccountFactory.create(t)
    ))
  }

  def create(socialAccountType: SocialAccountType, token: String, sessionId: SessionId): Future[Unit] = {
    socialAccountsDAO.exist(
      socialAccountType,
      sessionId
    ).flatMap(_ match {
      case false =>
        socialAccountsDAO.create(socialAccountType, token, sessionId).flatMap(_ => Future.Unit)
      case true =>
        Future.exception(CactaceaException(SocialAccountAlreadyConnected))
    })
  }

  def delete(socialAccountType: SocialAccountType, sessionId: SessionId): Future[Unit] = {
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
