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
      query[Clients]
        .filter(_.id == lift(clientId))
        .join(query[ClientGrantTypes]).on({ case (c, ocg) => ocg.clientId == c.id})
        .join(query[GrantTypes]).on({ case ((_, ocg), gt) => gt.id == ocg.grantTypeId && gt.grantType == lift(grantType)})
        .map({ case ((c, _), _) => c})
    }
    run(q).map(_.headOption)
  }

  def validateClient(id: String, secret: String, grantType: String): Future[Boolean] = {
    val q = quote {
      query[Clients]
        .filter(_.id == lift(id))
        .filter(_.secret.exists(_ == lift(secret) || lift(secret) == ""))
        .join(query[ClientGrantTypes]).on({ case (c, cgt) => cgt.clientId == c.id })
        .join(query[GrantTypes]).on({ case ((_, cgt), gt) => gt.id == cgt.grantTypeId && gt.grantType == lift(grantType) })
        .size
    }
    run(q).map(_ > 0)
  }


}
