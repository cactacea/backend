package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class ClientsDAO @Inject()(db: DatabaseService) {

  import db._

  def find(clientId: String, clientSecret: String): Future[Option[Clients]] = {
    val q = quote {
      query[Clients]
        .filter(c => c.id == lift(clientId) && c.secret == lift(clientSecret))
        .join(query[ClientGrantTypes]).on({ case (c, ocg) => ocg.clientId == c.id})
        .join(query[GrantTypes]).on({ case ((_, ocg), gt) => gt.id == ocg.grantTypeId})
        .map({ case ((c, _), _) => c})
    }
    run(q).map(_.headOption)
  }

  def exists(id: String, secret: String, grantType: String): Future[Boolean] = {
    val q = quote {
      (for {
        c <- query[Clients]
          .filter(_.id == lift(id))
          .filter(_.secret.exists(_ == lift(secret) || lift(secret) == lift("")))
        cgt <- query[ClientGrantTypes]
          .join(_.clientId == c.id)
        gt <- query[GrantTypes]
          .join(gt => gt.id == cgt.grantTypeId && gt.grantType == lift(grantType))
      } yield (gt)).size
    }
    run(q).map(_ > 0)
  }


}
