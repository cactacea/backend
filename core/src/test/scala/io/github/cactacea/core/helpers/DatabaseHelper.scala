package io.github.cactacea.backend.core.helpers

import com.twitter.util.Await
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.models._

object DatabaseHelper {

  def initialize(db: DatabaseService) = {
    import db._
    val r = db.transaction {
      for {
        _ <- run(quote(query[Notifications].delete))
        _ <- run(quote(query[AccountFeeds].delete))
        _ <- run(quote(query[AccountGroups].delete))
        _ <- run(quote(query[AccountMessages].delete))
        _ <- run(quote(query[AccountReports].delete))
        _ <- run(quote(query[AdvertisementSettings].delete))
        _ <- run(quote(query[Blocks].delete))
        _ <- run(quote(query[CommentReports].delete))
        _ <- run(quote(query[CommentLikes].delete))
        _ <- run(quote(query[FeedLikes].delete))
        _ <- run(quote(query[FeedMediums].delete))
        _ <- run(quote(query[FeedReports].delete))
        _ <- run(quote(query[FeedTags].delete))
        _ <- run(quote(query[FriendRequests].delete))
        _ <- run(quote(query[GroupInvitations].delete))
        _ <- run(quote(query[GroupReports].delete))
        _ <- run(quote(query[PushNotificationSettings].delete))
        _ <- run(quote(query[Relationships].delete))
        _ <- run(quote(query[SocialAccounts].delete))
        _ <- run(quote(query[Devices].delete))
        _ <- run(quote(query[Comments].delete))
        _ <- run(quote(query[Feeds].delete))
        _ <- run(quote(query[Messages].delete))
        _ <- run(quote(query[Groups].delete))
        _ <- run(quote(query[Accounts].delete))
        _ <- run(quote(query[Mediums].delete))
      } yield (Unit)
    }
    Await.result(r)
  }

}
