package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.IdentifyService

class DefaultIdentifyService @Inject()(db: DatabaseService) extends IdentifyService {

  import db._

  override def generate(): Future[Long] = {
    db.transaction {
      val q = quote(infix"""select generateId()""".as[Query[Long]])
      run(q).map({a =>
        a.head
      })
    }
  }

}

