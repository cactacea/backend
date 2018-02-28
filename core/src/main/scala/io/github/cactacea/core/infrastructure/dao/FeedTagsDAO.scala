package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.FeedId
import io.github.cactacea.core.infrastructure.models.FeedTags
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FeedTagsDAO @Inject()(db: DatabaseService) {

  import db._

  def findAll(feedIds: List[FeedId]): Future[List[FeedTags]] = {
    if (feedIds.size == 0) {
      Future.value(List[FeedTags]())
    } else {
      val q = quote {
        query[FeedTags]
          .filter(t => liftQuery(feedIds).contains(t.feedId))
          .sortBy(_.registerAt)
      }
      run(q)
    }
  }


  def create(feedId: FeedId, tagsOpt: Option[List[String]]) = {
    tagsOpt match {
      case Some(tags) =>
        val feedTags = tags.zipWithIndex.map({case (tag, index) => FeedTags(feedId, tag, index)})
        val q = quote {
          liftQuery(feedTags).foreach(c => query[FeedTags].insert(c))
        }
        run(q).map(_ => true)
      case None =>
        Future.True
    }
  }

  def delete(feedId: FeedId) = {
    val q = quote {
      query[FeedTags]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => true)
  }

}
