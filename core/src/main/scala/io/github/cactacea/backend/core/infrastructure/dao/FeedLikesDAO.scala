package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.domain.models.{User, Feed}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models.{FeedLikes, _}

@Singleton
class FeedLikesDAO @Inject()(db: DatabaseService) {

  import db._


  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- createFeedLikes(feedId, sessionId)
      _ <- updateLikeCount(feedId, 1L)
    } yield (())
  }

  private def createFeedLikes(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val likedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[FeedLikes]
        .insert(
          _.feedId -> lift(feedId),
          _.likedAt -> lift(likedAt),
          _.by -> lift(by)
        ).returning(_.id)
    }
    run(q).map(_ => ())
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFeedLikes(feedId, sessionId)
      _ <- updateLikeCount(feedId, -1L)
    } yield (())
  }

  private def deleteFeedLikes(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId == lift(feedId))
        .filter(_.by == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId == lift(feedId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def findUsers(feedId: FeedId,
                since: Option[Long],
                offset: Int,
                count: Int,
                sessionId: SessionId): Future[Seq[User]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        fl <- query[FeedLikes]
          .filter(_.feedId == lift(feedId))
          .filter(fl => lift(since).forall(fl.id < _))
          .filter(fl => query[Blocks].filter(b => b.userId == lift(by) && b.by == fl.by).isEmpty)
        a <- query[Users]
          .join(_.id == fl.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (a, r, fl.id))
        .sortBy({ case (_, _, id) => id })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (a, r, id) => User(a, r, id.value) }))
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Feed]] = { // scalastyle:ignore
    val e = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      (for {
        fl <- query[FeedLikes]
          .filter(_.by == lift(userId))
          .filter(fl => lift(since).forall(fl.id < _))
        (f, flb, fcb) <- query[Feeds]
          .join(_.id == fl.feedId)
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => query[Blocks].filter(b => b.userId == lift(by) && b.by == f.by).isEmpty)
          .filter(f => (
            f.by == lift(by)) || (f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships]
              .filter(_.userId == f.by)
              .filter(_.by == lift(by))
              .filter(r =>
                ((r.follow || r.isFriend) && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                  (r.isFriend && (f.privacyType == lift(FeedPrivacyType.friends)))
              ).nonEmpty))
          .map(f =>
            (f,
              query[FeedLikes]
                .filter(_.feedId == f.id)
                .filter(fl => query[Blocks].filter(b => b.userId == lift(by) && b.by == fl.by).nonEmpty).size,
              query[Comments]
                .filter(_.feedId == f.id)
                .filter(c => query[Blocks].filter(b => b.userId == lift(by) && b.by == c.by).nonEmpty).size))
        l <- query[FeedLikes].leftJoin(fl => fl.feedId == f.id && fl.by == lift(by))
        i1 <- query[Mediums].leftJoin(_.id == f.mediumId1)
        i2 <- query[Mediums].leftJoin(_.id == f.mediumId2)
        i3 <- query[Mediums].leftJoin(_.id == f.mediumId3)
        i4 <- query[Mediums].leftJoin(_.id == f.mediumId4)
        i5 <- query[Mediums].leftJoin(_.id == f.mediumId5)
        a <- query[Users]
          .join(_.id == f.by)
          .filter(a => query[Blocks].filter(b => (b.userId == lift(by) && b.by == a.id)).isEmpty)
        r <- query[Relationships]
          .leftJoin(r => r.userId == f.by && r.by == lift(by))
      } yield (fl, l, f, i1, i2, i3, i4, i5, a, r, flb, fcb))
        .sortBy({ case (fl, _, _, _, _, _, _, _, _, _, _, _) => fl.feedId})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (fl, l, f, i1, i2, i3, i4, i5, a, r, flb, fcb) =>
      val f2 = f.copy(commentCount = f.commentCount - fcb, likeCount = f.likeCount - flb)
      Feed(f2, l, Seq(i1, i2, i3, i4, i5).flatten, a, r, fl.feedId.value)
    }))
  }

  private def updateLikeCount(feedId: FeedId, plus: Long): Future[Unit] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.likeCount -> (a.likeCount + lift(plus))
        )
    }
    run(q).map(_ => ())
  }


}
