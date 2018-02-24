package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.FriendsRepository
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FriendsService @Inject()(db: DatabaseService) {

  @Inject var friendsRepository: FriendsRepository = _

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    friendsRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    friendsRepository.findAll(since, offset, count, sessionId)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      friendsRepository.delete(accountId, sessionId)
    }
  }

}
