package io.github.cactacea.backend.auth.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.core.infrastructure.models.Authentications
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class AuthenticationsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(providerId: String, providerKey: String, password: String, hasher: String): Future[Unit] = {
    val q = quote {
      query[Authentications].insert(
        _.providerId  -> lift(providerId),
        _.providerKey -> lift(providerKey),
        _.password    -> lift(password),
        _.hasher      -> lift(hasher),
        _.confirm     -> false
      )
    }
    run(q).map(_ => ())
  }

  def updatePassword(providerId: String, providerKey: String, password: String, hasher: String): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .update(
          _.password  -> lift(password),
          _.hasher    -> lift(hasher)
        )
    }
    run(q).map(_ => ())
  }

  def updateConfirm(providerId: String, providerKey: String, confirm: Boolean): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .update(
          _.confirm    -> lift(confirm)
        )
    }
    run(q).map(_ => ())
  }

  def updateUserId(providerId: String, providerKey: String, userId: UserId): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .update(
          _.userId    -> lift(Option(userId))
        )
    }
    run(q).map(_ => ())
  }

  def updateProviderKey(providerId: String, oldProviderKey: String, newProviderKey: String): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(oldProviderKey))
        .update(
          _.providerKey    -> lift(newProviderKey)
        )
    }
    run(q).map(_ => ())
  }

  def updateProviderKey(providerId: String, userId: Option[UserId], newProviderKey: String): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.userId == lift(userId))
        .update(
          _.providerKey    -> lift(newProviderKey)
        )
    }
    run(q).map(_ => ())
  }

  def exists(providerId: String, providerKey: String): Future[Boolean] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .nonEmpty
    }
    run(q)
  }

  def exists(providerId: String, providerKey: String, userId: UserId): Future[Boolean] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .filter(_.userId == lift(Option(userId)))
        .nonEmpty
    }
    run(q)
  }

  def exists(providerId: String, userId: UserId): Future[Boolean] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.userId == lift(Option(userId)))
        .nonEmpty
    }
    run(q)
  }

  def find(providerId: String, providerKey: String): Future[Option[Authentication]] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
    }
    run(q).map(_.headOption.map(Authentication(_)))
  }

  def find(providerId: String, userId: UserId): Future[Option[Authentication]] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.userId == lift(Option(userId)))
    }
    run(q).map(_.headOption.map(Authentication(_)))
  }

  def find(userName: String, userId: Option[UserId]): Future[Option[Authentication]] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(CredentialsProvider.ID))
        .filter(_.providerKey == lift(userName))
        .filter(_.userId == lift(userId))
    }
    run(q).map(_.headOption.map(Authentication(_)))
  }

  //  def find(providerId: String, providerKey: String): Future[Option[User]] = {
//    val q = quote {
//      for {
//        au <- query[Authentications]
//          .filter(_.providerId == lift(providerId))
//          .filter(_.providerKey == lift(providerKey))
//        a <- query[Users]
//          .filter(_.id == au.userId)
//      } yield (a)
//    }
//    run(q).map(_.headOption.map(User(_)))
//  }
//

  def delete(providerId: String, providerKey: String): Future[Unit] = {
    val q = quote {
      query[Authentications]
        .filter(_.providerId == lift(providerId))
        .filter(_.providerKey == lift(providerKey))
        .delete
    }
    run(q).map(_ => ())
  }

}
