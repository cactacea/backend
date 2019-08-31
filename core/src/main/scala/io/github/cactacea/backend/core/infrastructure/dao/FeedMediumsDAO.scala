package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, MediumId}
import io.github.cactacea.backend.core.infrastructure.models.{FeedMediums, Mediums}

@Singleton
class FeedMediumsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(feedId: FeedId, mediumIdsOpt: Option[List[MediumId]]): Future[Unit] = {
    mediumIdsOpt.fold(Future.Unit) { mediumIds =>
      val feedImages = mediumIds.zipWithIndex.map({case (mediumId, index) => FeedMediums(feedId, mediumId, index)})
      val q = quote {
        liftQuery(feedImages).foreach(c => query[FeedMediums].insert(c))
      }
      run(q).map(_ => ())
    }
  }

  def delete(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedMediums]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => ())
  }


}
