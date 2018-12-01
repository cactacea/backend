package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, FeedPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, FeedLikeId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{FeedLikes, _}

@Singleton
class FeedLikesDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFeedLikes(feedId, sessionId)
      r <- updateLikeCount(feedId, 1L)
    } yield (r)
  }

  private def insertFeedLikes(feedId: FeedId, sessionId: SessionId): Future[FeedLikeId] = {
    val likedAt = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedLikes]
        .insert(
          _.feedId   -> lift(feedId),
          _.likedAt -> lift(likedAt),
          _.by       -> lift(by)
        ).returning(_.id)
    }
    run(q)
  }

  private def updateLikeCount(feedId: FeedId, count: Long): Future[Unit] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.likeCount -> (a.likeCount + lift(count))
        )
    }
    run(q).map(_ => Unit)
  }

  def delete(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFeedLikes(feedId, sessionId)
      r <- updateLikeCount(feedId, -1L)
    } yield (r)
  }

  private def deleteFeedLikes(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId  == lift(feedId))
        .filter(_.by      == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def deleteLikes(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
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
    run(q).map(_ => Unit)
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

  def findAccounts(feedId: FeedId,
                   since: Option[Long],
                   offset: Int,
                   count: Int,
                   sessionId: SessionId): Future[List[(Accounts, Option[Relationships], FeedLikes)]] = {

    val s = since.getOrElse(-1L)


    val by = sessionId.toAccountId
    val status = AccountStatusType.normally

    val q = quote {
      query[FeedLikes]
        .filter(f => f.feedId == lift(feedId) && (f.likedAt < lift(s) || lift(s) == -1L))
        .filter(ff =>
          query[Blocks]
            .filter(_.accountId == lift(by))
            .filter(_.by        == ff.by)
            .isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.by && a.accountStatus  == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f)})
        .sortBy({ case (_, _, f) => f.likedAt })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q)

  }

  def findAll(since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[(Feeds, FeedLikes)]] = {

    val s = since.getOrElse(-1L)


    val by = sessionId.toAccountId

    val q = quote {
      query[FeedLikes].filter(f => f.by == lift(by) && (f.likedAt < lift(s) || lift(s) == -1L))
        .filter(f => query[Blocks].filter(b => b.accountId == lift(by) && b.by == f.by).isEmpty)
        .filter(f => query[Blocks].filter(b => b.accountId == f.by && b.by == lift(by)).isEmpty)
        .join(query[Feeds]).on((ff, f) => f.id == ff.feedId &&
        ((f.privacyType == lift(FeedPrivacyType.everyone))
        || (f.privacyType == lift(FeedPrivacyType.followers)
          && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.follow == true)).nonEmpty))
        || (f.privacyType == lift(FeedPrivacyType.friends)
          && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
        || (f.by == lift(by))))
        .map({case (ff, f) => (f, ff)})
        .sortBy({ case (ff, _) => ff.postedAt})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q)

  }

  def findAll(accountId: AccountId,
              since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[(Feeds, FeedLikes)]] = {

    val s = since.getOrElse(-1L)


    val by = sessionId.toAccountId

    val q = quote {
      query[FeedLikes].filter(ff => ff.by == lift(accountId) && (ff.likedAt < lift(s) || lift(s) == -1L))
        .filter(f => query[Blocks].filter(b => b.accountId == lift(by) && b.by == f.by).isEmpty)
        .filter(f => query[Blocks].filter(b => b.accountId == f.by && b.by == lift(by)).isEmpty)
        .join(query[Feeds]).on((ff, f) => f.id == ff.feedId &&
        ((f.privacyType == lift(FeedPrivacyType.everyone))
        || (f.privacyType == lift(FeedPrivacyType.followers)
            && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.follow == true)).nonEmpty))
        || (f.privacyType == lift(FeedPrivacyType.friends)
            && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
        || (f.by == lift(by))))
        .map({case (ff, f) => (f, ff)})
        .sortBy({ case (ff, _) => ff.postedAt})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q)

  }

}
