package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao.FollowersDAO
import io.github.cactacea.core.infrastructure.identifiers.{SessionId, AccountId}

@Singleton
class FollowersRepository {

  @Inject private var followersDAO: FollowersDAO = _

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    val accountId = sessionId.toAccountId
    followersDAO.findAll(accountId, since, offset, count, sessionId)
      .map(_.map({ case (a, r, n) => Account(a, r, n)}))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followersDAO.findAll(accountId, since, offset, count, sessionId)
      .map(_.map({ case (a, r, n) => Account(a, r, n)}))
  }

}
