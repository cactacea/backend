package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, FeedPrivacyType}
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FeedsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[FeedId] = {

    for {
      r <- createFeeds(message, mediumIds, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- updateFeedCount(1L, sessionId)
    } yield (r)
  }

  private def createFeeds(message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[FeedId] = {

    val by = sessionId.toAccountId
    val postedAt = System.currentTimeMillis()
    val t = tags.map(_.mkString(" "))
    val i1 = mediumIds.flatMap(_.lift(0))
    val i2 = mediumIds.flatMap(_.lift(1))
    val i3 = mediumIds.flatMap(_.lift(2))
    val i4 = mediumIds.flatMap(_.lift(3))
    val i5 = mediumIds.flatMap(_.lift(4))
    val q = quote {
      query[Feeds].insert(
        _.by                  -> lift(by),
        _.message             -> lift(message),
        _.tags                -> lift(t),
        _.mediumId1           -> lift(i1),
        _.mediumId2           -> lift(i2),
        _.mediumId3           -> lift(i3),
        _.mediumId4           -> lift(i4),
        _.mediumId5           -> lift(i5),
        _.likeCount           -> 0L,
        _.commentCount        -> 0L,
        _.expiration          -> lift(expiration),
        _.privacyType         -> lift(privacyType),
        _.contentWarning      -> lift(contentWarning),
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returning(_.id)
    }
    run(q)

  }

  def update(feedId: FeedId,
             message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[Unit] = {

    val by = sessionId.toAccountId
    val privacy = privacyType
    val t = tags.map(_.mkString(" "))
    val i1 = mediumIds.flatMap(_.lift(0))
    val i2 = mediumIds.flatMap(_.lift(1))
    val i3 = mediumIds.flatMap(_.lift(2))
    val i4 = mediumIds.flatMap(_.lift(3))
    val i5 = mediumIds.flatMap(_.lift(4))
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .filter(_.by == lift(by))
        .update(
          _.message         -> lift(message),
          _.tags            -> lift(t),
          _.mediumId1       -> lift(i1),
          _.mediumId2       -> lift(i2),
          _.mediumId3       -> lift(i3),
          _.mediumId4       -> lift(i4),
          _.mediumId5       -> lift(i5),
          _.contentWarning  -> lift(contentWarning),
          _.expiration      -> lift(expiration),
          _.privacyType     -> lift(privacy)
        )
    }
    run(q).map(_ => ())

  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFeeds(feedId, sessionId)
      _ <- updateFeedCount(-1L, sessionId)
    } yield (())
  }

  private def deleteFeeds(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(_.by == lift(by))
        .filter(_.id == lift(feedId))
        .delete
    }
    run(q).map(_ => ())
  }



  def own(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .filter(_.by == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def exists(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val e = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .filter(f => f.expiration.forall(_ > lift(e)))
        .filter(f =>
          query[Blocks].filter(b => (b.accountId == lift(by) && b.by == f.by)).isEmpty)
        .filter({ f =>
          (f.by == lift(by)) ||
          (f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships]
              .filter(_.accountId == f.by)
              .filter(_.by == lift(by))
              .filter(r =>
              ((r.follow || r.isFriend) && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                (r.isFriend && (f.privacyType == lift(FeedPrivacyType.friends)))
            ).nonEmpty)
            })
        .nonEmpty
    }
    run(q)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {  // scalastyle:ignore
    val e = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      (for {
        (f, flb, fcb) <- query[Feeds]
          .filter(f => f.by == lift(accountId))
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => lift(since).forall(f.id < _))
          .filter(f => (
              f.by == lift(by)) || (f.privacyType == lift(FeedPrivacyType.everyone)) ||
              (query[Relationships]
                .filter(_.accountId == f.by)
                .filter(_.by == lift(by))
                .filter(r =>
                  ((r.follow || r.isFriend) && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                    (r.isFriend && (f.privacyType == lift(FeedPrivacyType.friends)))
                ).nonEmpty))
          .map(f =>
            (f,
              query[FeedLikes]
                .filter(_.feedId == f.id)
                .filter(fl =>
                  query[Blocks].filter(b => b.accountId == lift(by) && b.by == fl.by).nonEmpty
                ).size,
              query[Comments]
                .filter(_.feedId == f.id)
                .filter(c =>
                  query[Blocks].filter(b => b.accountId == lift(by) && b.by == c.by).nonEmpty
                ).size))
        l <- query[FeedLikes].leftJoin(fl => fl.feedId == f.id && fl.by == lift(by))
        i1 <- query[Mediums].leftJoin(_.id == f.mediumId1)
        i2 <- query[Mediums].leftJoin(_.id == f.mediumId2)
        i3 <- query[Mediums].leftJoin(_.id == f.mediumId3)
        i4 <- query[Mediums].leftJoin(_.id == f.mediumId4)
        i5 <- query[Mediums].leftJoin(_.id == f.mediumId5)
        a <- query[Accounts]
          .join(a => a.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb))
        .sortBy(_._1.id)(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({ case (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb) =>
      val f2 = f.copy(commentCount = f.commentCount - fcb, likeCount = f.likeCount - flb)
      Feed(f2, l, List(i1, i2, i3, i4, i5).flatten, a, r, f.id.value)
    }))
  }


  def find(feedId: FeedId, sessionId: SessionId): Future[Option[Feed]] = {  // scalastyle:ignore
    val by = sessionId.toAccountId
    val e = System.currentTimeMillis()
    val q = quote {
      (for {
        (f, flb, fcb) <- query[Feeds]
          .filter(f => f.id == lift(feedId))
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => query[Blocks].filter(b => (b.accountId == lift(by) && b.by == f.by)).isEmpty)
          .filter(f => (
            f.by == lift(by)) || (f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships]
              .filter(_.accountId == f.by)
              .filter(_.by == lift(by))
              .filter(r =>
                ((r.follow || r.isFriend) && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                  (r.isFriend && (f.privacyType == lift(FeedPrivacyType.friends)))
              ).nonEmpty))
          .map(f =>
            (f, query[FeedLikes]
              .filter(_.feedId == f.id)
              .filter(fl =>
                query[Blocks].filter(b => b.accountId == lift(by) && b.by == fl.by).nonEmpty
              ).size,
              query[Comments]
                .filter(_.feedId == f.id)
                .filter(c =>
                  query[Blocks].filter(b => b.accountId == lift(by) && b.by == c.by).nonEmpty
                ).size))
        l <- query[FeedLikes].leftJoin(fl => fl.feedId == f.id && fl.by == lift(by))
        i1 <- query[Mediums].leftJoin(_.id == f.mediumId1)
        i2 <- query[Mediums].leftJoin(_.id == f.mediumId2)
        i3 <- query[Mediums].leftJoin(_.id == f.mediumId3)
        i4 <- query[Mediums].leftJoin(_.id == f.mediumId4)
        i5 <- query[Mediums].leftJoin(_.id == f.mediumId5)
        a <- query[Accounts]
          .join(_.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb))
    }
    run(q).map(_.headOption.map({ case (f, l, i1, i2, i3, i4, i5, a, r, flb, fcb) =>
      val f2 = f.copy(commentCount = f.commentCount - fcb, likeCount = f.likeCount - flb)
      Feed(f2, l, List(i1, i2, i3, i4, i5).flatten, a, r, f.id.value)
    }))
  }

  private def updateFeedCount(plus: Long, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.feedCount -> (a.feedCount + lift(plus))
        )
    }
    run(q).map(_ => ())
  }

  def findOwner(feedId: FeedId): Future[Option[AccountId]] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .map(_.by)
    }
    run(q).map(_.headOption)
  }

}

