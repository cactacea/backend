package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.core.infrastructure.models.FeedTags

@Singleton
class FeedTagsDAO @Inject()(db: DatabaseService) {

  import db._

  def find(feedIds: List[FeedId]): Future[List[FeedTags]] = {
    if (feedIds.size == 0) {
      Future.value(List[FeedTags]())
    } else {
      val q = quote {
        query[FeedTags]
          .filter(t => liftQuery(feedIds).contains(t.feedId))
          .sortBy(_.orderNo)
      }
      run(q)
    }
  }

}
