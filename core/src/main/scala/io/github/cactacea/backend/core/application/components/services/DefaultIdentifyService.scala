package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{ConfigService, IdentifyService}
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

class DefaultIdentifyService @Inject()(db: DatabaseService, config: ConfigService) extends IdentifyService {

  import db._

  override def generate(): Future[Long] = {
    db.transaction {
      val q = quote(infix"""select generateId(1)""".as[Query[Long]])
      run(q).map({a =>
        a.head
      })
    }
  }

  override def generate(accountId: AccountId): Future[Long] = {
    db.transaction {
      val shardId = accountId.value % config.numberOfShards
      val q = quote(infix"""select generateId(${lift(shardId)})""".as[Query[Long]])
      run(q).map({a =>
        a.head
      })
    }
  }

}

