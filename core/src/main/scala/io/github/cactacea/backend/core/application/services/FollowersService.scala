package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.FollowersRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, AccountId}

@Singleton
class FollowersService {

  @Inject private var followersRepository: FollowersRepository = _

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followersRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followersRepository.findAll(since, offset, count, sessionId)
  }
}
