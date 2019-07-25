package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FriendsSortType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.FriendsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

class FriendsService @Inject()(
                                databaseService: DatabaseService,
                                friendsRepository: FriendsRepository
                              ) {

  import databaseService._

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    friendsRepository.find(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Int, count: Int, sortType: FriendsSortType, sessionId: SessionId) : Future[List[Account]]= {
    friendsRepository.find(since, offset, count, sortType, sessionId)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    transaction{
      friendsRepository.delete(accountId, sessionId)
    }
  }

}
