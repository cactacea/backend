package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.FriendsRepository
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FriendsService {

  @Inject private var db: DatabaseService = _
  @Inject private var friendsRepository: FriendsRepository = _
  @Inject private var actionService: InjectionService = _

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    friendsRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    friendsRepository.findAll(since, offset, count, sessionId)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- friendsRepository.delete(accountId, sessionId)
        _ <- actionService.accountUnFriended(accountId, sessionId)
      } yield (r)
    }
  }

}
