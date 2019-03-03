package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, ContentStatusType, FeedPrivacyType}
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FeedsDAO @Inject()(
                          db: DatabaseService,
                          feedTagsDAO: FeedTagsDAO,
                          feedMediumDAO: FeedMediumDAO,
                          commentsDAO: CommentsDAO
                        ) {

  import db._

  def create(message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[FeedId] = {

    val by = sessionId.toAccountId
    for {
      id  <- insertFeed(message, privacyType, contentWarning, expiration, by)
      _ <- updateAccount(1L, sessionId)
      _  <- createTags(id, tags)
      _  <- feedMediumDAO.create(id, mediumIds)
    } yield (id)
  }

  private def insertFeed(message: String,
                         privacyType: FeedPrivacyType,
                         contentWarning: Boolean,
                         expiration: Option[Long],
                         by: AccountId): Future[FeedId] = {

    val privacy = privacyType
    val postedAt = System.currentTimeMillis()
    val q = quote {
      query[Feeds].insert(
        _.by                  -> lift(by),
        _.message             -> lift(message),
        _.likeCount           -> 0L,
        _.commentCount        -> 0L,
        _.expiration          -> lift(expiration),
        _.privacyType         -> lift(privacy),
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
    for {
      _ <- updateFeeds(feedId, message, privacyType, contentWarning, expiration, by)
      _ <- deleteTags(feedId)
      _ <- deleteMediums(feedId)
      _ <- createTags(feedId, tags)
      _ <- feedMediumDAO.create(feedId, mediumIds)
    } yield (Unit)
  }

  private def updateFeeds(feedId: FeedId,
                          message: String,
                          privacyType: FeedPrivacyType,
                          contentWarning: Boolean,
                          expiration: Option[Long],
                          by: AccountId): Future[Unit] = {

    val privacy = privacyType
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .filter(_.by == lift(by))
        .update(
          _.message         -> lift(message),
          _.contentWarning  -> lift(contentWarning),
          _.expiration      -> lift(expiration),
          _.privacyType     -> lift(privacy)
        )
    }
    run(q).map(_ => Unit)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    for {
      _ <- deleteTags(feedId)
      _ <- deleteLikes(feedId)
      _ <- deleteMediums(feedId)
      _ <- deleteReports(feedId)
      _ <- commentsDAO.delete(feedId)
      r <- deleteFeeds(feedId, by)
      _ <- updateAccount((r * -1L), sessionId)
    } yield (())
  }

  private def deleteFeeds(feedId: FeedId, by: AccountId): Future[Long] = {
    val q = quote {
      query[Feeds]
        .filter(_.by == lift(by))
        .filter(_.id == lift(feedId))
        .delete
    }
    run(q)
  }

  private def updateAccount(count: Long, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          a => a.feedCount -> (a.feedCount + lift(count))
        )
    }
    run(q).map(_ => Unit)
  }

  def exist(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val e = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .filter(f =>
          f.expiration.forall(_ > lift(e)) ||
          f.expiration.isEmpty)
        .filter(f => query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == f.by) ||
            (b.accountId == f.by && b.by == lift(by))
        ).isEmpty)
        .filter({ f =>
          (f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(r =>
              (r.following == true && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                (r.isFriend == true && (f.privacyType == lift(FeedPrivacyType.friends)))
            ).nonEmpty) ||
            (f.by == lift(by))})
        .nonEmpty
    }
    run(q)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {

    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(f => f.by == lift(by))
        .filter(f => lift(since).forall(f.id < _))
        .sortBy(_.id)(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).flatMap(addTagsMedium(_))
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Option[Feed]] = {
    val by = sessionId.toAccountId
    val e = System.currentTimeMillis()
    val q = quote {
      (for {
        (f, flb, fcb) <- query[Feeds]
          .filter(f => f.id == lift(feedId))
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => query[Blocks].filter(b =>
              (b.accountId == lift(by) && b.by == f.by) ||
              (b.accountId == f.by && b.by == lift(by))
          ).isEmpty)
          .filter(f =>
            (f.by == lift(by)) ||
            (f.privacyType == lift(FeedPrivacyType.everyone)) ||
            (query[Relationships]
              .filter(_.accountId == f.by)
              .filter(_.by == lift(by))
              .filter(r =>
                (r.following && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                (r.isFriend && (f.privacyType == lift(FeedPrivacyType.friends)))
            ).nonEmpty))
            .map(f =>
              (f,
                query[FeedLikes]
                  .filter(_.feedId == f.id)
                  .filter(fl =>
                    query[Blocks].filter(b =>
                      (b.accountId == lift(by) && b.by == fl.by) || (b.accountId == fl.by && b.by == lift(by))
                    ).nonEmpty
                  ).size,
                query[Comments]
                  .filter(_.feedId == f.id)
                  .filter(c =>
                    query[Blocks].filter(b =>
                      (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
                    ).nonEmpty
                  ).size
              )
            )
        a <- query[Accounts]
          .join(a => a.id == f.by && a.accountStatus  == lift(AccountStatusType.normally))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, a, r, flb, fcb))
    }
    run(q).flatMap(addTagsMedium2(_)).map(_.headOption)
  }

  def find(accountId: AccountId,
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[List[Feed]] = {
    val e = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      (for {
        (f, flb, cb) <- query[Feeds]
          .filter(f => f.by == lift(accountId))
          .filter(f => f.expiration.forall(_ > lift(e)))
          .filter(f => lift(since).forall(f.id < _))
          .filter(f =>
              (f.by == lift(by)) ||
              (f.privacyType == lift(FeedPrivacyType.everyone)) ||
              (query[Relationships]
                .filter(_.accountId == f.by)
                .filter(_.by == lift(by))
                .filter(r =>
                  (r.following && (f.privacyType == lift(FeedPrivacyType.followers))) ||
                    (r.isFriend && (f.privacyType == lift(FeedPrivacyType.friends)))
                ).nonEmpty))
          .map(f =>
            (f,
              query[FeedLikes]
                .filter(_.feedId == f.id)
                .filter(fl =>
                  query[Blocks].filter(b => (b.accountId == lift(by) && b.by == fl.by) || (b.accountId == fl.by && b.by == lift(by))
                  ).nonEmpty
                ).size,
              query[Comments]
                .filter(_.feedId == f.id)
                .filter(c =>
                  query[Blocks].filter(b => (b.accountId == lift(by) && b.by == c.by) || (b.accountId == c.by && b.by == lift(by))
                  ).nonEmpty
                ).size
            )
          )
        a <- query[Accounts]
          .join(a => a.id == f.by && a.accountStatus  == lift(AccountStatusType.normally))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, a, r, flb, cb))
        .sortBy({ case (f, _, _, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).flatMap(addTagsMedium2(_))
  }

  private def addTagsMedium2(t: List[(Feeds, Accounts, Option[Relationships], Long, Long)]): Future[List[Feed]] = {
    val feedIds = t.map({ case (f, _, _, _, _) => f.id})
    (for {
      tags <- feedTagsDAO.find(feedIds)
      medium <- feedMediumDAO.find(feedIds)
    } yield (tags, medium)).map({
      case (tags, medium) =>
        t.map({ case (f, a, r, flb, cb) =>
          val t = tags.filter(_.feedId == f.id)
          val m = medium.filter({ case (id, _) => id == f.id}).map({ case (_, m) => m })
          Feed(f.copy(commentCount =  f.commentCount - cb, likeCount = f.likeCount - flb), t, m, a, r, f.id.value)
        })
    })
  }

  private def addTagsMedium(feeds: List[Feeds]): Future[List[Feed]] = {
    val feedIds = feeds.map(_.id)
    (for {
      tags <- feedTagsDAO.find(feedIds)
      medium <- feedMediumDAO.find(feedIds)
    } yield (tags, medium)).map({
      case (tags, medium) =>
        feeds.map({ f =>
          val t = tags.filter(_.feedId == f.id)
          val m = medium.filter({ case (id, _) => f.id == id}).map({ case (_, m) => m })
          Feed(f, t, m, f.id.value)
        })
    })
  }

  private def deleteTags(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedTags]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def deleteLikes(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedLikes]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def deleteMediums(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedMediums]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def deleteReports(feedId: FeedId): Future[Unit] = {
    val q = quote {
      query[FeedReports]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => Unit)
  }

  private def createTags(feedId: FeedId, tagsOpt: Option[List[String]]): Future[Unit] = {
    tagsOpt match {
      case Some(tags) =>
        val feedTags = tags.zipWithIndex.map({case (tag, index) => FeedTags(feedId, tag, index)})
        val q = quote {
          liftQuery(feedTags).foreach(c => query[FeedTags].insert(c))
        }
        run(q).map(_ => Unit)
      case None =>
        Future.Unit
    }
  }

}

