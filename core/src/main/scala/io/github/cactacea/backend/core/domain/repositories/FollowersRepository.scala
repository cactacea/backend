package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.{FollowersDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowersRepository @Inject()(
                                     followersDAO: FollowersDAO,
                                     validationDAO: ValidationDAO
                                   ) {

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    (for {
      _ <- validationDAO.existAccount(accountId)
      r <- followersDAO.findAll(accountId, since, offset, count, sessionId)
    } yield (r)).map(_.map({ case (a, r, f) => Account(a, r, f)}))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followersDAO.findAll(since, offset, count, sessionId)
      .map(_.map({ case (a, r, f) => Account(a, r, f)}))
  }

}
