package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AuthDAO @Inject()(db: DatabaseService) {

  import db._

  def findClient(clientId: String): Future[Option[Clients]] = {
    val grantType = "authorization_code"
    val q = quote {
      (for {
        oc <- query[Clients]
          .filter(_.id == lift(clientId))
        ocg <- query[ClientGrantTypes]
          .filter(_.clientId == oc.id)
        og <- query[GrantTypes]
          .filter(_.id == ocg.grantTypeId).filter(_.grantType == lift(grantType))
      } yield (oc))
    }
    run(q).map(_.headOption)
  }

  def validateClient(id: String, secret: String, grantType: String): Future[Boolean] = {
    val q = quote {
      (for {
        oc <- query[Clients]
          .filter(_.id == lift(id))
          .filter(_.secret.exists(_ == lift(secret) || lift(secret) == ""))
        ocg <- query[ClientGrantTypes]
          .filter(_.clientId == oc.id)
        og <- query[GrantTypes]
          .filter(_.id == ocg.grantTypeId).filter(_.grantType == lift(grantType))
      } yield (oc, ocg, og)).size
    }
    run(q).map(_ > 0)
  }

}
