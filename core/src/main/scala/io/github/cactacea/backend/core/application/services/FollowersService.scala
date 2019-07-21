package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.FollowersRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, AccountId}

class FollowersService @Inject()(followersRepository: FollowersRepository) {

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followersRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followersRepository.find(since, offset, count, sessionId)
  }
}
