package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.db.DatabaseService

@Singleton
class IdentifiesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(): Future[Long] = {
    val q = quote {
      infix"""select generateId()""".as[Query[Long]]
    }
    run(q).map(_.head)
  }
}
