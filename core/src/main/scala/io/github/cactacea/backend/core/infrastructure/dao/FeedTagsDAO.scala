package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.core.infrastructure.models.FeedTags

@Singleton
class FeedTagsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(feedId: FeedId, tagsOpt: Option[Seq[String]]): Future[Unit] = {
    tagsOpt match {
      case Some(tags) =>
        val feedTags = tags.zipWithIndex.map({case (tag, index) => FeedTags(feedId, tag, index)})
        val q = quote {
          liftQuery(feedTags).foreach(c => query[FeedTags].insert(c))
        }
        run(q).map(_ => ())
      case None =>
        Future.Unit
    }
  }

  def delete(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedTags]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => ())
  }

}
