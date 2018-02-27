package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.models.SocialAccounts
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class SocialAccountsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(socialAccountType: SocialAccountType, socialAccountId: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .insert(
          _.accountId           -> lift(accountId),
          _.socialAccountId     -> lift(socialAccountId),
          _.socialAccountType   -> lift(socialAccountType.toValue)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(accountType: SocialAccountType, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.socialAccountType == lift(accountType.toValue))
        .delete
    }
    run(q).map(_ == 1)
  }

  def find(accountType: SocialAccountType, socialAccountId: String): Future[Option[SocialAccounts]] = {
    val q = quote {
      query[SocialAccounts]
        .filter(_.socialAccountId == lift(socialAccountId))
        .filter(_.socialAccountType == lift(accountType.toValue))
    }
    run(q).map(_.headOption)
  }

  def exist(accountType: SocialAccountType, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.socialAccountType == lift(accountType.toValue))
        .size
    }
    run(q).map(_ == 1)
  }

    def findAll(sessionId: SessionId): Future[List[SocialAccounts]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.socialAccountType != lift(0L))
        .sortBy(_.socialAccountType)
    }
    run(q)
  }

}
