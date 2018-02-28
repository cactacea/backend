package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{AccountStatusType, FeedPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, SessionId}
import io.github.cactacea.core.infrastructure.models.{FeedFavorites, _}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FeedFavoritesDAO @Inject()(db: DatabaseService) {

  @Inject var feedsDAO: FeedsDAO = _

  import db._

  def create(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _insertFeedFavorites(feedId, sessionId)
      r <- _updateFavoriteCount(feedId)
    } yield (r)
  }

  private def _insertFeedFavorites(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val postedAt = System.nanoTime()
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedFavorites]
        .insert(
          _.feedId   -> lift(feedId),
          _.postedAt -> lift(postedAt),
          _.by       -> lift(by)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateFavoriteCount(feedId: FeedId): Future[Boolean] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.favoriteCount -> (a.favoriteCount + 1)
        )
    }
    run(q).map(_ == 1)
  }

  def delete(feedId: FeedId) = {
    val q = quote {
      query[FeedFavorites]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => true)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    for {
      _ <- _deleteFeedFavorites(feedId, sessionId)
      r <- _updateUnfavoriteCount(feedId)
    } yield (r)
  }

  private def _deleteFeedFavorites(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedFavorites]
        .filter(_.feedId  == lift(feedId))
        .filter(_.by      == lift(by))
        .delete
    }
    run(q).map(_ == 1)
  }

  private def _updateUnfavoriteCount(feedId: FeedId): Future[Boolean] = {
    val q = quote {
      query[Feeds]
        .filter(_.id == lift(feedId))
        .update(
          a => a.favoriteCount -> (a.favoriteCount - 1)
        )
    }
    run(q).map(_ == 1)
  }

  def deleteFavorites(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedFavorites]
        .filter(feed_favorites => feed_favorites.by == lift(by))
        .filter(feed_favorites => query[Feeds]
          .filter(_.id      == feed_favorites.feedId)
          .filter(_.by      == lift(accountId))
          .nonEmpty)
        .delete
    }
    run(q).map(_ >= 0)
  }

  def exist(feedId: FeedId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedFavorites]
        .filter(_.feedId  == lift(feedId))
        .filter(_.by      == lift(by))
        .take(1)
        .size
    }
    run(q).map(_ == 1)
  }

  def findAccounts(feedId: FeedId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships])]] = {

    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId
    val status = AccountStatusType.singedUp.toValue

    val q = quote {
      query[FeedFavorites].filter(ff => ff.feedId == lift(feedId) && ff.postedAt < lift(s) &&
        query[Blocks].filter(b => b.accountId == ff.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty)
        .join(query[Accounts]).on((ff, a) => a.id == ff.by && a.accountStatus  == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .sortBy({ case ((ff, _), _) => ff.postedAt })(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }
    run(q).map(_.map({ case ((ff, a), r) => (a.copy(position = ff.postedAt), r)}))

  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feeds]] = {
    val s = since.getOrElse(Long.MaxValue)
    val o = offset.getOrElse(0)
    val c = count.getOrElse(20)
    val by = sessionId.toAccountId

    val q = quote {
      query[FeedFavorites].filter(ff => ff.by == lift(by) && ff.postedAt < lift(s))
        .join(query[Feeds]).on((ff, f) => f.id == ff.feedId &&
        query[Blocks].filter(b => b.accountId == f.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty &&
        ((f.privacyType == lift(FeedPrivacyType.everyone.toValue))
        || (f.privacyType == lift(FeedPrivacyType.followers.toValue) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.followed == true)).nonEmpty))
        || (f.privacyType == lift(FeedPrivacyType.friends.toValue)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
        || (f.by == lift(by))))
        .sortBy({ case (ff, _) => ff.postedAt })(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }

    run(q).map(_.map({case (ff, f) => f.copy(postedAt = ff.postedAt)}))

  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feeds]] = {

    val s = since.getOrElse(Long.MaxValue)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)
    val by = sessionId.toAccountId

    val q = quote {
      query[FeedFavorites].filter(ff => ff.by == lift(accountId) && ff.postedAt < lift(s))
        .join(query[Feeds]).on((ff, f) => f.id == ff.feedId &&
        (query[Blocks].filter(b => b.accountId == f.by && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty) &&
        ((f.privacyType == lift(FeedPrivacyType.everyone.toValue))
        || (f.privacyType == lift(FeedPrivacyType.followers.toValue) && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.followed == true)).nonEmpty))
        || (f.privacyType == lift(FeedPrivacyType.friends.toValue)   && ((query[Relationships].filter(_.accountId == f.by).filter(_.by == lift(by)).filter(_.friend == true)).nonEmpty))
        || (f.by == lift(by))))
        .sortBy({ case (ff, _) => ff.postedAt })(Ord.descNullsLast)
        .drop(lift(o))
        .take(lift(c))
    }

    run(q).map(_.map({case (ff, f) => f.copy(postedAt = ff.postedAt)}))

  }

}
