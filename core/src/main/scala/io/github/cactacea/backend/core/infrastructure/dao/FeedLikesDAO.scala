package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, FeedPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, FeedLikeId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{FeedLikes, _}

@Singleton
class FeedLikesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _insertFeedLikes(feedId, sessionId)
      r <- _updateLikeCount(feedId)
    } yield (r)
  }

  private def _insertFeedLikes(feedId: FeedId, sessionId: SessionId): Future[FeedLikeId] = {
    val postedAt = timeService.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedLikes]
        .insert(
          _.feedId   -> lift(feedId),
          _.postedAt -> lift(postedAt),
          _.by       -> lift(by)
        ).returning(_.id)
    }
    run(q)
  }

  private def _updateLikeCount(feedId: FeedId): Future[Boolean] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.likeCount -> (a.likeCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(feedId: FeedId) = {
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => true)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _deleteFeedLikes(feedId, sessionId)
      r <- _updateUnlikeCount(feedId)
    } yield (r)
  }

  private def _deleteFeedLikes(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId  == lift(feedId))
        .filter(_.by      == lift(by))
        .delete
    }
    run(q).map(_ == 1)
  }

  private def _updateUnlikeCount(feedId: FeedId): Future[Boolean] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.likeCount -> (a.likeCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  def deleteLikes(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedLikes]
        .filter(feed_likes => feed_likes.by == lift(by))
        .filter(feed_likes => query[Feeds]
          .filter(_.id      == feed_likes.feedId)
          .filter(_.by      == lift(accountId))
          .nonEmpty)
        .delete
    }
    run(q).map(_ >= 0)
  }

  def exist(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId  == lift(feedId))
        .filter(_.by      == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships], FeedLikes)]] = {

    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId
    val status = AccountStatusType.normally

    val q = quote {
      query[FeedLikes].filter(ff => ff.feedId == lift(feedId) && (ff.id < lift(s) || lift(s) == -1L) &&
        query[Blocks].filter(b => b.accountId == ff.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty)
        .join(query[Accounts]).on((ff, a) => a.id == ff.by && a.accountStatus  == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((ff, a), r) => (a, r, ff)})
        .sortBy(_._3.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Feeds, FeedLikes)]] = {
    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[FeedLikes].filter(ff => ff.by == lift(by) && (ff.id < lift(s) || lift(s) == -1L))
        .join(query[Feeds]).on((ff, f) => f.id == ff.feedId &&
        query[Blocks].filter(b => b.accountId == f.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty &&
        ((f.privacyType == lift(FeedPrivacyType.everyone))
        || (f.privacyType == lift(FeedPrivacyType.followers) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.follow == true)).nonEmpty))
        || (f.privacyType == lift(FeedPrivacyType.friends)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
        || (f.by == lift(by))))
        .map({case (ff, f) => (f, ff)})
        .sortBy({ case (ff, _) => ff.id })(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Feeds, FeedLikes)]] = {

    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId

    val q = quote {
      query[FeedLikes].filter(ff => ff.by == lift(accountId) && (ff.id < lift(s) || lift(s) == -1L))
        .join(query[Feeds]).on((ff, f) => f.id == ff.feedId &&
        (query[Blocks].filter(b => b.accountId == f.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty) &&
        ((f.privacyType == lift(FeedPrivacyType.everyone))
        || (f.privacyType == lift(FeedPrivacyType.followers) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.follow == true)).nonEmpty))
        || (f.privacyType == lift(FeedPrivacyType.friends)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
        || (f.by == lift(by))))
        .map({case (ff, f) => (f, ff)})
        .sortBy({ case (ff, _) => ff.id })(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q)

  }

}
