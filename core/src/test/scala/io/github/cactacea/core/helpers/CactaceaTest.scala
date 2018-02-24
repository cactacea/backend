package io.github.cactacea.core.helpers

import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import com.twitter.util.logging.Logging
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.util.modules.{PushNotificationServiceProviderModule, QueueServiceProviderModule, StorageServiceProviderModule}
import io.github.cactacea.core.util.provider.module.DatabaseProviderModule
import org.scalatest.BeforeAndAfter

class CactaceaTest extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        PushNotificationServiceProviderModule,
        QueueServiceProviderModule,
        StorageServiceProviderModule,
        FinatraJacksonModule
      )
    ).create

  before {
    initialize()
  }

  after {
    initialize()
  }


  def initialize() = {
    val db = DatabaseUtil.db
    import db._
    val r = db.transaction {
      for {
        _ <- db.run(quote(query[AccountFeeds].delete))
        _ <- db.run(quote(query[AccountGroups].delete))
        _ <- db.run(quote(query[AccountMessages].delete))
        _ <- db.run(quote(query[AccountReports].delete))
        _ <- db.run(quote(query[AdvertisementSettings].delete))
        _ <- db.run(quote(query[Blocks].delete))
        _ <- db.run(quote(query[CommentFavorites].delete))
        _ <- db.run(quote(query[CommentReports].delete))
        _ <- db.run(quote(query[FeedFavorites].delete))
        _ <- db.run(quote(query[FeedMediums].delete))
        _ <- db.run(quote(query[FeedReports].delete))
        _ <- db.run(quote(query[FeedTags].delete))
        _ <- db.run(quote(query[FriendRequests].delete))
        _ <- db.run(quote(query[GroupInvites].delete))
        _ <- db.run(quote(query[GroupReports].delete))
        _ <- db.run(quote(query[PushNotificationSettings].delete))
        _ <- db.run(quote(query[Relationships].delete))
        _ <- db.run(quote(query[SocialAccounts].delete))
        _ <- db.run(quote(query[Timelines].delete))
        _ <- db.run(quote(query[Devices].delete))
        _ <- db.run(quote(query[Comments].delete))
        _ <- db.run(quote(query[Feeds].delete))
        _ <- db.run(quote(query[Messages].delete))
        _ <- db.run(quote(query[Groups].delete))
        _ <- db.run(quote(query[Accounts].delete))
        _ <- db.run(quote(query[Mediums].delete))
      } yield (Unit)
    }
    Await.result(r)
  }

}
