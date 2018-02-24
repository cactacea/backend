package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.domain.models.SocialAccount
import io.github.cactacea.core.domain.repositories.SocialAccountsRepository
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class SocialAccountsService @Inject()(db: DatabaseService) {

  @Inject var socialAccountsRepository: SocialAccountsRepository = _

  def connect(socialAccountType: SocialAccountType, sessionId: SessionId, token: String): Future[Unit] = {
    db.transaction {
      socialAccountsRepository.create(socialAccountType, token, sessionId)
    }
  }

  def disconnect(socialAccountType: SocialAccountType, sessionId: SessionId): Future[Unit] = {
    db.transaction (
      socialAccountsRepository.delete(socialAccountType, sessionId)
    )
  }

  def find(sessionId: SessionId): Future[List[SocialAccount]] = {
    socialAccountsRepository.findAll(sessionId)
  }

}
