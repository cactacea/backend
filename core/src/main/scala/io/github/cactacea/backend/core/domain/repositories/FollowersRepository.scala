package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.FollowersDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.AccountsValidator


class FollowersRepository @Inject()(
                                     accountsValidator: AccountsValidator,
                                     followersDAO: FollowersDAO
                                   ) {

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    (for {
      _ <- accountsValidator.exist(accountId, sessionId)
      r <- followersDAO.find(accountId, since, offset, count, sessionId)
    } yield (r))

  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followersDAO.find(since, offset, count, sessionId)
  }

}
