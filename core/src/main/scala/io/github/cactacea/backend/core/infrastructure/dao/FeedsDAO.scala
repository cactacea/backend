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
                          feedLikesDAO: FeedLikesDAO,
                          feedReportsDAO: FeedReportsDAO,
                          commentsDAO: CommentsDAO,
                          blocksCountDAO: BlockCountDAO
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
      _  <- feedTagsDAO.create(id, tags)
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
      _ <- feedTagsDAO.delete(feedId)
      _ <- feedMediumDAO.delete(feedId)
      _ <- feedTagsDAO.create(feedId, tags)
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
      _ <- feedTagsDAO.delete(feedId)
      _ <- feedLikesDAO.delete(feedId)
      _ <- feedMediumDAO.delete(feedId)
      _ <- feedReportsDAO.delete(feedId)
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
          a => a.feedsCount -> (a.feedsCount + lift(count))
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
    run(q).flatMap(f => addTagsMedium(f, sessionId))
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Option[Feed]] = {
    val by = sessionId.toAccountId
    val e = System.currentTimeMillis()
    val q = quote {
      (for {
        f <- query[Feeds]
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
        a <- query[Accounts]
          .join(a => a.id == f.by && a.accountStatus  == lift(AccountStatusType.normally))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, a, r))
    }

    run(q).flatMap({ t => addTagsMedium2(t, sessionId).map(_.headOption) })
  }

  private def addTagsMedium2(t: List[(Feeds, Accounts, Option[Relationships])], sessionId: SessionId): Future[List[Feed]] = {
    val feedIds = t.map({ case (f, _, _) => f.id})
    (for {
      tags <- feedTagsDAO.find(feedIds)
      medium <- feedMediumDAO.find(feedIds)
      likeBlocks <- blocksCountDAO.findFeedLikeBlocks(feedIds, sessionId)
      commentBlocks <- blocksCountDAO.findFeedCommentBlocks(feedIds, sessionId)
    } yield (tags, medium, likeBlocks, commentBlocks)).map({
      case (tags, medium, likeBlocks, commentBlocks) =>
        t.map({ case (f, a, r) =>
          val t = tags.filter(_.feedId == f.id)
          val m = medium.filter({ case (id, _) => id == f.id}).map({ case (_, m) => m })
          val fb = likeBlocks.filter(_.id == f.id).map(_.count).headOption
          val cb = commentBlocks.filter(_.id == f.id).map(_.count).headOption
          val nf = f.copy(commentCount = f.commentCount - cb.getOrElse(0L), likeCount = f.likeCount - fb.getOrElse(0L) )
          Feed(nf, t, m, a, r, f.id.value)
        })
    })
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
        f <- query[Feeds]
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
        a <- query[Accounts]
          .join(a => a.id == f.by && a.accountStatus  == lift(AccountStatusType.normally))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (f, a, r))
        .sortBy({ case (f, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))

    }
    run(q).flatMap(f => addTagsMedium2(f, sessionId))
  }

  private def addTagsMedium(feeds: List[Feeds], sessionId: SessionId): Future[List[Feed]] = {
    val feedIds = feeds.map(_.id)
    (for {
      tags <- feedTagsDAO.find(feedIds)
      medium <- feedMediumDAO.find(feedIds)
      likeBlocks <- blocksCountDAO.findFeedLikeBlocks(feedIds, sessionId)
      commentBlocks <- blocksCountDAO.findFeedCommentBlocks(feedIds, sessionId)
    } yield (tags, medium, likeBlocks, commentBlocks)).map({
      case (tags, medium, likeBlocks, commentBlocks) =>
        feeds.map({ f =>
          val t = tags.filter(_.feedId == f.id)
          val m = medium.filter({ case (id, _) => f.id == id}).map({ case (_, m) => m })
          val fb = likeBlocks.filter(_.id == f.id).map(_.count).headOption
          val cb = commentBlocks.filter(_.id == f.id).map(_.count).headOption
          val cf = f.copy(commentCount = f.commentCount - cb.getOrElse(0L), likeCount = f.likeCount - fb.getOrElse(0L) )
          Feed(cf, t, m, cf.id.value)
        })
    })
  }

}

