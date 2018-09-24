package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, MediumId}
import io.github.cactacea.backend.core.infrastructure.models.{FeedMediums, Mediums}

@Singleton
class FeedMediumDAO @Inject()(db: DatabaseService) {

  import db._

  def findAll(feedIds: List[FeedId]): Future[List[(FeedId, Mediums)]] = {
    if (feedIds.size == 0) {
      Future.value(List[(FeedId, Mediums)]())
    } else {
      val q = quote {
        query[FeedMediums].filter(t => liftQuery(feedIds).contains(t.feedId))
          .join(query[Mediums]).on((f, m) => m.id == f.mediumId)
          .sortBy(_._1.orderNo)
          .map({ case (f, m) => (f.feedId, m) })
      }
      run(q)
    }
  }

  def create(feedId: FeedId, mediumIdsOpt: Option[List[MediumId]]) = {
    mediumIdsOpt match {
      case Some(mediumIds) =>
        val feedImages = mediumIds.zipWithIndex.map({case (mediumId, index) => FeedMediums(feedId, mediumId, index)})
        val q = quote {
          liftQuery(feedImages).foreach(c => query[FeedMediums].insert(c))
        }
        run(q).map(_ => true)
      case None =>
        Future.True
    }
  }

  def delete(feedId: FeedId) = {
    val q = quote {
      query[FeedMediums]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ >= 0)
  }

}
