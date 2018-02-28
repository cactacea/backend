package io.github.cactacea.core.infrastructure.dao

import java.util.Date

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class AuthDAO @Inject()(db: DatabaseService) {

  import db._

  def findByCode(code: String): Future[Option[AuthCodes]] = {
    val q = quote {
      query[AuthCodes]
        .filter(_.authorizationCode == lift(code))
    }
    db.run(q).map(_.headOption)
  }

  def deleteByCode(code: String): Future[Unit] = {
    val q = quote {
      query[AuthCodes]
        .filter(_.authorizationCode == lift(code))
    }
    run(q).flatMap(_ => Future.Unit)
  }

  def createCode(code: String, redirectUri: Option[String], scope: Option[String], clientId: String, accountId: AccountId, createdAt: Long, expiresIn: Long): Future[Unit] = {
    val q = quote {
      query[AuthCodes].insert(
        _.authorizationCode -> lift(code),
        _.redirectUri -> lift(redirectUri),
        _.scope -> lift(scope),
        _.clientId -> lift(clientId),
        _.accountId -> lift(accountId),
        _.createdAt -> lift(createdAt),
        _.expiresIn -> lift(expiresIn)
      )
    }
    run(q).flatMap(_ => Future.Unit)
  }

  def findByToken(token: String): Future[Option[AccessTokens]] = {
    val q = quote {
      query[AccessTokens]
        .filter(_.accessToken == lift(token))
    }
    db.run(q).map(_.headOption)
  }

  def findByRefreshToken(token: String): Future[Option[AccessTokens]] = {
    val q = quote {
      query[AccessTokens]
        .filter(_.refreshToken.forall(_ == lift(token)))
    }
    db.run(q).map(_.headOption)
  }

  def createToken(accessToken: String, refreshToken: Option[String], accountId: AccountId, clientId: String, scope: Option[String], expiresIn: Long, createdAt: Date): Future[Unit] = {
    val q = quote {
      query[AccessTokens]
        .insert(
          _.accessToken -> lift(accessToken),
          _.accountId -> lift(accountId),
          _.clientId -> lift(clientId),
          _.expiresIn -> lift(expiresIn),
          _.refreshToken -> lift(refreshToken),
          _.scope -> lift(scope)
        )
    }
    run(q).map(_ => Unit)
  }

  def deleteByRefreshToken(refreshToken: String): Future[Unit] = {
    val q = quote {
      query[AccessTokens]
        .filter(_.refreshToken.forall(_ == lift(refreshToken)))
    }
    run(q).flatMap(_ => Future.Unit)
  }

  def validateClient(id: String, secret: String, grantType: String): Future[Boolean] = {
    val q = quote {
      (for {
        oc <- query[Clients]
          .filter(_.id == lift(id))
          .filter(_.secret.forall(_ == lift(secret)))
        ocg <- query[ClientGrantTypes]
          .filter(_.clientId == oc.id)
        og <- query[GrantTypes]
          .filter(_.id == ocg.grantTypeId).filter(_.grantType == lift(grantType))
      } yield (oc, ocg, og)).size
    }
    run(q).map(_ > 0)
  }

  def validateRedirectUri(id: String, redirectUri: String): Future[Boolean] = {
    val q = quote {
      query[Clients]
        .filter(_.id == lift(id))
        .filter(_.redirectUri.forall(_ == lift(redirectUri)))
        .size
    }
    run(q).map(_ > 0)
  }

}
