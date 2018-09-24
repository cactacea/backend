package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, ContentStatusType, FeedPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FeedsDAO @Inject()(db: DatabaseService) {

  @Inject private var feedTagsDAO: FeedTagsDAO = _
  @Inject private var feedMediumDAO: FeedMediumDAO = _
  @Inject private var feedLikesDAO: FeedLikesDAO = _
  @Inject private var feedReportsDAO: FeedReportsDAO = _
  @Inject private var commentsDAO: CommentsDAO = _
  @Inject private var blocksCountDAO: BlockCountDAO = _
  @Inject private var timeService: TimeService = _

  import db._

  def create(message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], sessionId: SessionId): Future[FeedId] = {
    val by = sessionId.toAccountId
    for {
      id  <- _insertFeed(message, privacyType, contentWarning, expiration, by)
      _  <- feedTagsDAO.create(id, tags)
      _  <- feedMediumDAO.create(id, mediumIds)
    } yield (id)
  }

  private def _insertFeed(message: String, privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], by: AccountId): Future[FeedId] = {
    val privacy = privacyType
    val postedAt = timeService.currentTimeMillis()
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

  def update(feedId: FeedId, message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    _updateFeeds(feedId, message, privacyType, contentWarning, expiration, by).flatMap(_ match {
      case true =>
        for {
          t1 <- feedTagsDAO.delete(feedId)
          m1 <- feedMediumDAO.delete(feedId)
          t2 <- feedTagsDAO.create(feedId, tags)
          m2 <- feedMediumDAO.create(feedId, mediumIds)
        } yield (t1 && m1 && t2 && m2)
      case false =>
        Future.False
    })
  }

  private def _updateFeeds(feedId: FeedId, message: String, privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], by: AccountId): Future[Boolean] = {
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
    run(q).map(_ == 1)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    for {
      _ <- feedTagsDAO.delete(feedId)
      _ <- feedLikesDAO.delete(feedId)
      _ <- feedMediumDAO.delete(feedId)
      _ <- feedReportsDAO.delete(feedId)
      _ <- commentsDAO.delete(feedId)
      r <- _deleteFeeds(feedId, by)
    } yield (r)
  }

  private def _deleteFeeds(feedId: FeedId, by: AccountId) = {
    val q = quote {
      query[Feeds]
        .filter(_.by == lift(by))
        .filter(_.id == lift(feedId))
        .delete
    }
    run(q).map(_ == 1)
  }


  def exist(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val e = timeService.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .filter({ f => f.expiration.forall(_ > lift(e)) || f.expiration.isEmpty})
        .filter(t => query[Blocks]
          .filter(_.accountId == lift(by))
          .filter(_.by          == t.by)
          .isEmpty)
        .filter(f =>
          (f.privacyType == lift(FeedPrivacyType.everyone))
            || (f.privacyType == lift(FeedPrivacyType.followers) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.follow == true)).nonEmpty))
            || (f.privacyType == lift(FeedPrivacyType.friends)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
            || (f.by == lift(by)))
        .nonEmpty
    }
    run(q)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Feeds, List[FeedTags], List[Mediums])]] = {
    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(_.by        ==  lift(by))
        .filter(_.id < lift(s) || lift(s) == -1L)
        .sortBy(_.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).flatMap(f => _addTagsMedium(f, sessionId)) //.map(_.sortWith(_._1.id.value > _._1.id.value)))
  }


  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Feeds, List[FeedTags], List[Mediums])]] = {

    val e = timeService.currentTimeMillis()
    val s = since.getOrElse(-1L)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId
    val q = quote {
      query[Feeds]
        .filter(_.by == lift(accountId))
        .filter(f =>
          (f.privacyType == lift(FeedPrivacyType.everyone))
            || (f.privacyType == lift(FeedPrivacyType.followers) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.follow == true)).nonEmpty))
            || (f.privacyType == lift(FeedPrivacyType.friends)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
            || (f.by == lift(by)))
        .filter({ f => f.expiration.forall(_ > lift(e)) || f.expiration.isEmpty })
        .filter(_.id < lift(s) || lift(s) == -1L)
        .sortBy(_.id)(Ord.desc)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).flatMap(f => _addTagsMedium(f, sessionId)) // .map(_.sortWith(_._1.id.value > _._1.id.value)))
  }

  private def _addTagsMedium(feeds: List[Feeds], sessionId: SessionId) = {
    val feedIds = feeds.map(_.id)
    (for {
      tags <- feedTagsDAO.findAll(feedIds)
      medium <- feedMediumDAO.findAll(feedIds)
      likeBlocks <- blocksCountDAO.findFeedLikeBlocks(feedIds, sessionId)
      commentBlocks <- blocksCountDAO.findFeedCommentBlocks(feedIds, sessionId)
    } yield (tags, medium, likeBlocks, commentBlocks)).map({
      case (tags, medium, likeBlocks, commentBlocks) =>
        feeds.map({ f =>
          val t = tags.filter(_.feedId == f.id)
          val m = medium.filter(_._1 == f.id).map(_._2)
          val fb = likeBlocks.filter(_.id == f.id).map(_.count).headOption
          val cb = commentBlocks.filter(_.id == f.id).map(_.count).headOption
          val nf = f.copy(commentCount = f.commentCount - cb.getOrElse(0L), likeCount = f.likeCount - fb.getOrElse(0L) )
          (nf, t, m)
        })
    })
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Option[(Feeds, List[FeedTags], List[Mediums], Accounts, Option[Relationships])]] = {
    val by = sessionId.toAccountId
    val status = AccountStatusType.normally
    val e = timeService.currentTimeMillis()
    val q = quote {
      query[Feeds].filter(f => f.id == lift(feedId) && (f.expiration.forall(_ > lift(e)) || f.expiration.isEmpty))
        .filter(f => query[Blocks]
          .filter(_.accountId == lift(by))
          .filter(_.by        == f.by)
          .isEmpty)
        .filter(f => ((f.privacyType == lift(FeedPrivacyType.everyone))
          || (f.privacyType == lift(FeedPrivacyType.followers) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.follow == true)).nonEmpty))
          || (f.privacyType == lift(FeedPrivacyType.friends)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
          || (f.by == lift(by))))
        .join(query[Accounts]).on((ff, a) => a.id == ff.by && a.accountStatus  == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
    }

    run(q).flatMap({ t =>
      val feedIds = t.map(_._1._1.id)
      (for {
        tags <- feedTagsDAO.findAll(feedIds)
        medium <- feedMediumDAO.findAll(feedIds)
        likeBlocks <- blocksCountDAO.findFeedLikeBlocks(feedIds, sessionId)
        commentBlocks <- blocksCountDAO.findFeedCommentBlocks(feedIds, sessionId)
      } yield (tags, medium, likeBlocks, commentBlocks)).map({
        case (tags, medium, likeBlocks, commentBlocks) =>
          t.map({ case ((f, a), r) =>
            val t = tags.filter(_.feedId == f.id)
            val m = medium.filter(_._1 == f.id).map(_._2)
            val fb = likeBlocks.filter(_.id == f.id).map(_.count).headOption
            val cb = commentBlocks.filter(_.id == f.id).map(_.count).headOption
            val nf = f.copy(commentCount = f.commentCount - cb.getOrElse(0L), likeCount = f.likeCount - fb.getOrElse(0L) )
            (nf, t, m, a, r)
          }).headOption
      })
    })
  }

  def find(feedId: FeedId): Future[Option[Feeds]] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
    }
    run(q).map(_.headOption)
  }

  def updateNotified(feedId: FeedId, notified: Boolean): Future[Boolean] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ == 1)
  }

}

