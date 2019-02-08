package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, MediumId}
import io.github.cactacea.backend.core.infrastructure.models.{FeedMediums, Mediums}

@Singleton
class FeedMediumDAO @Inject()(db: DatabaseService) {

  import db._

  def find(feedIds: List[FeedId]): Future[List[(FeedId, Mediums)]] = {
    if (feedIds.isEmpty) {
      Future.value(List[(FeedId, Mediums)]())
    } else {
      val q = quote {
        (for {
          fm <- query[FeedMediums]
            .filter(fm => liftQuery(feedIds).contains(fm.feedId))
          m <- query[Mediums]
              .join(_.id == fm.mediumId)
        } yield (fm, m))
          .sortBy({ case (f, _) => f.orderNo})
          .map({ case (f, m) => (f.feedId, m) })
      }
      run(q)
    }
  }

  def create(feedId: FeedId, mediumIdsOpt: Option[List[MediumId]]): Future[Unit] = {
    mediumIdsOpt match {
      case Some(mediumIds) =>
        val feedImages = mediumIds.zipWithIndex.map({case (mediumId, index) => FeedMediums(feedId, mediumId, index)})
        val q = quote {
          liftQuery(feedImages).foreach(c => query[FeedMediums].insert(c))
        }
        run(q).map(_ => Unit)
      case None =>
        Future.Unit
    }
  }

  def delete(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedMediums]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => Unit)
  }

}
