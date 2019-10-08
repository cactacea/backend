package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.TweetId
import io.github.cactacea.backend.core.infrastructure.models.TweetTags

@Singleton
class TweetTagsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(tweetId: TweetId, tagsOpt: Option[Seq[String]]): Future[Unit] = {
    tagsOpt match {
      case Some(tags) =>
        val tweetTags = tags.zipWithIndex.map({case (tag, index) => TweetTags(tweetId, tag, index)})
        val q = quote {
          liftQuery(tweetTags).foreach(c => query[TweetTags].insert(c))
        }
        run(q).map(_ => ())
      case None =>
        Future.Unit
    }
  }

  def delete(tweetId: TweetId): Future[Unit] = {
    val q = quote {
      query[TweetTags]
        .filter(_.tweetId == lift(tweetId))
        .delete
    }
    run(q).map(_ => ())
  }

}
