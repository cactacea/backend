package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.{FollowersDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowersRepository {

  @Inject private var followersDAO: FollowersDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    for {
      _ <- validationDAO.existAccount(accountId)
      r <- followersDAO.findAll(accountId, since, offset, count, sessionId)
        .map(_.map( t => Account(t._1, t._2, t._3.followedAt)))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followersDAO.findAll(since, offset, count, sessionId)
      .map(_.map( t => Account(t._1, t._2, t._3.followedAt)))
  }

}
