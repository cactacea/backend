package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.FollowersRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, AccountId}

class FollowersService @Inject()(followersRepository: FollowersRepository) {

  def find(accountId: AccountId, accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followersRepository.find(accountId, accountName, since, offset, count, sessionId)
  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followersRepository.find(accountName, since, offset, count, sessionId)
  }
}
