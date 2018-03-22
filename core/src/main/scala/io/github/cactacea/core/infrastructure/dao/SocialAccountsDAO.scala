package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.models.SocialAccounts

@Singleton
class SocialAccountsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(socialAccountType: String, socialAccountId: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .insert(
          _.accountId           -> lift(accountId),
          _.socialAccountId     -> lift(socialAccountId),
          _.socialAccountType   -> lift(socialAccountType)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(accountType: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.socialAccountType == lift(accountType))
        .delete
    }
    run(q).map(_ == 1)
  }

  def find(accountType: String, socialAccountId: String): Future[Option[SocialAccounts]] = {
    val q = quote {
      query[SocialAccounts]
        .filter(_.socialAccountId == lift(socialAccountId))
        .filter(_.socialAccountType == lift(accountType))
    }
    run(q).map(_.headOption)
  }

  def exist(accountType: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.socialAccountType == lift(accountType))
        .size
    }
    run(q).map(_ == 1)
  }

  def findAll(sessionId: SessionId): Future[List[SocialAccounts]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[SocialAccounts]
        .filter(_.accountId == lift(accountId))
        .filter(_.socialAccountType != "")
        .sortBy(_.socialAccountType)
    }
    run(q)
  }

}
