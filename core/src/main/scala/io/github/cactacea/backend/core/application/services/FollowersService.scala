package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.FollowersRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{SessionId, UserId}

@Singleton
class FollowersService @Inject()(followersRepository: FollowersRepository) {

  def find(userId: UserId, userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    followersRepository.find(userId, userName, since, offset, count, sessionId)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    followersRepository.find(userName, since, offset, count, sessionId)
  }
}
