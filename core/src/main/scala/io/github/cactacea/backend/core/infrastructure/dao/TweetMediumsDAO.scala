package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId, MediumId}
import io.github.cactacea.backend.core.infrastructure.models.{TweetMediums}

@Singleton
class TweetMediumsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(tweetId: TweetId, mediumIdsOpt: Option[Seq[MediumId]]): Future[Unit] = {
    mediumIdsOpt.fold(Future.Unit) { mediumIds =>
      val tweetImages = mediumIds.zipWithIndex.map({case (mediumId, index) => TweetMediums(tweetId, mediumId, index)})
      val q = quote {
        liftQuery(tweetImages).foreach(c => query[TweetMediums].insert(c))
      }
      run(q).map(_ => ())
    }
  }

  def delete(tweetId: TweetId): Future[Unit] = {
    val q = quote {
      query[TweetMediums]
        .filter(_.tweetId == lift(tweetId))
        .delete
    }
    run(q).map(_ => ())
  }


}
