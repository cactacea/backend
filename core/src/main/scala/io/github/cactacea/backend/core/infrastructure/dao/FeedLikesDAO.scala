package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, FeedPrivacyType}
import io.github.cactacea.backend.core.domain.models.{Account, Feed}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models.{FeedLikes, _}

@Singleton
class FeedLikesDAO @Inject()(db: DatabaseService, timeService: TimeService) {

  import db._

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- insertFeedLikes(feedId, sessionId)
      _ <- updateLikeCount(feedId, 1L)
    } yield (Unit)
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
      _ <- updateLikeCount(feedId, -1L)
    } yield (Unit)
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
                   sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId
    val status = AccountStatusType.normally

    val q = quote {
      query[FeedLikes]
        .filter(f => f.feedId == lift(feedId))
        .filter(f => lift(since).forall(f.id  < _))
        .filter(ff => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == ff.by) || (b.accountId == ff.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Accounts]).on((f, a) => a.id == f.by && a.accountStatus  == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (a, r, f.id)})
        .sortBy({ case (_, _, id) => id })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => Account(a, r, id.value)}))


  }

  def findAll(since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[Feed]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[FeedLikes]
        .filter(ff => ff.by == lift(by))
        .filter(ff => lift(since).forall(ff.id < _))
        .filter(ff => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == ff.by) || (b.accountId == ff.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Feeds]).on({(ff, f) => f.id == ff.feedId &&
          (f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(r =>
              (r.following == true && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                (r.isFriend == true && (f.privacyType == lift(FeedPrivacyType.friends)))
            ).nonEmpty) ||
            (f.by == lift(by))})
        .map({case (ff, f) => (f, ff)})
        .sortBy({ case (ff, _) => ff.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, ff) => Feed(f, ff, ff.id.value)}))

  }

  def findAll(accountId: AccountId,
              since: Option[Long],
              offset: Int,
              count: Int,
              sessionId: SessionId): Future[List[Feed]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[FeedLikes]
        .filter(ff => ff.by == lift(accountId))
        .filter(ff => lift(since).forall(ff.id  < _))
        .filter(ff => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == ff.by) || (b.accountId == ff.by && b.by == lift(by))
        ).isEmpty)
        .join(query[Feeds]).on({(ff, f) => f.id == ff.feedId &&
        ((f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(r =>
              (r.following == true && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                (r.isFriend == true && (f.privacyType == lift(FeedPrivacyType.friends)))
            ).nonEmpty) ||
            (f.by == lift(by)))})
        .map({case (ff, f) => (f, ff)})
        .sortBy({ case (ff, _) => ff.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, ff) => Feed(f, ff, ff.id.value)}))

  }

}
