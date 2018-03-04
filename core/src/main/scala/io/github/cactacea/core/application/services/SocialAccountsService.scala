package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.domain.models.SocialAccount
import io.github.cactacea.core.domain.repositories.SocialAccountsRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class SocialAccountsService @Inject()(db: DatabaseService, socialAccountsRepository: SocialAccountsRepository, injectionService: InjectionService) {

  def connect(socialAccountType: SocialAccountType, sessionId: SessionId, token: String): Future[Unit] = {
    for {
      r <- db.transaction(socialAccountsRepository.create(socialAccountType, token, sessionId))
      _ <- injectionService.socialAccountConnected(socialAccountType, sessionId)
    } yield (r)
  }

  def disconnect(socialAccountType: SocialAccountType, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(socialAccountsRepository.delete(socialAccountType, sessionId))
      _ <- injectionService.socialAccountDisconnected(socialAccountType, sessionId)
    } yield (r)
  }

  def find(sessionId: SessionId): Future[List[SocialAccount]] = {
    socialAccountsRepository.findAll(sessionId)
  }

}
