package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.AccountFeeds

class AccountFeedsDAOSpec extends DAOSpec {

  import db._

  val feedsDAO: FeedsDAO = injector.instance[FeedsDAO]
  val accountFeedsDAO: AccountFeedsDAO = injector.instance[AccountFeedsDAO]
  val followersDAO: FollowersDAO = injector.instance[FollowersDAO]

  test("create") {

    val sessionAccount1 = createAccount("AccountFeedsDAOSpec0")
    val sessionAccount2 = createAccount("AccountFeedsDAOSpec1")
    val sessionAccount3 = createAccount("AccountFeedsDAOSpec2")
    val sessionAccount4 = createAccount("AccountFeedsDAOSpec3")
    val sessionAccount5 = createAccount("AccountFeedsDAOSpec4")
    val sessionAccount6 = createAccount("AccountFeedsDAOSpec5")
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.self
    val contentWarning = true

    // create feed
    val feedId = Await.result(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionAccount2.id.toSessionId))

    // create follows
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount3.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount4.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount5.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount6.id.toSessionId))

    // create account feeds
    Await.result(accountFeedsDAO.create(feedId, sessionAccount2.id.toSessionId))

    val q = quote { query[AccountFeeds].filter(_.feedId == lift(feedId)).size}
    val result = Await.result(db.run(q))
    assert(result == 5)

  }

  test("update") {

    val sessionAccount1 = createAccount("AccountFeedsDAOSpec6")
    val sessionAccount2 = createAccount("AccountFeedsDAOSpec7")
    val sessionAccount3 = createAccount("AccountFeedsDAOSpec8")
    val sessionAccount4 = createAccount("AccountFeedsDAOSpec9")
    val sessionAccount5 = createAccount("AccountFeedsDAOSpec10")
    val sessionAccount6 = createAccount("AccountFeedsDAOSpec11")
    val medium1 = this.createMedium(sessionAccount1.id)
    val medium2 = this.createMedium(sessionAccount1.id)
    val medium3 = this.createMedium(sessionAccount1.id)
    val message = "message"
    val mediums = List(medium1.id, medium2.id, medium3.id)
    val tags = List("tag1", "tag2", "tag3")
    val privacyType = FeedPrivacyType.self
    val contentWarning = true

    // create feed
    val feedId = Await.result(feedsDAO.create(message, Some(mediums), Some(tags), privacyType, contentWarning, None, sessionAccount2.id.toSessionId))

    // create follows
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount1.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount3.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount4.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount5.id.toSessionId))
    Await.result(followersDAO.create(sessionAccount2.id, sessionAccount6.id.toSessionId))

    // create account feeds
    Await.result(accountFeedsDAO.create(feedId, sessionAccount2.id.toSessionId))

    val ids =  List(
      sessionAccount1.id,
      sessionAccount3.id,
      sessionAccount4.id,
      sessionAccount5.id,
      sessionAccount6.id
    )

    // update notified
    Await.result(accountFeedsDAO.update(feedId, ids, true))

    //
    val q = quote { query[AccountFeeds].filter(_.feedId == lift(feedId)).filter(_.notified == true).size}
    val result = Await.result(db.run(q))
    assert(result == ids.size)


  }

}
