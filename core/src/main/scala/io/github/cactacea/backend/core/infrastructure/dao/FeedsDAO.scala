package io.github.cactacea.backend.core.infrastructure.dao

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{DeepLinkService, MessageService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{FeedType}
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FeedsDAO @Inject()(db: DatabaseService,
                         deepLinkService: DeepLinkService,
                         messageService: MessageService,
                                 ) {

  import db._

  def create(id: InvitationId, userId: UserId, sessionId: SessionId): Future[FeedId] = {
    val by = sessionId.userId
    val url = deepLinkService.getInvitation(id)
    insert(userId, by, FeedType.invitation, id.value, url)
  }

  def create(id: FriendRequestId, userId: UserId, sessionId: SessionId): Future[FeedId] = {
    val by = sessionId.userId
    val url = deepLinkService.getRequest(id)
    insert(userId, by, FeedType.friendRequest, id.value, url)
  }

  def create(tweetId: TweetId, commentId: CommentId, userId: UserId, commentReply: Boolean, sessionId: SessionId): Future[FeedId] = {
    val by = sessionId.userId
    val feedType = commentReply match {
      case true => FeedType.commentReply
      case false => FeedType.tweetReply
    }
    val url = deepLinkService.getComment(tweetId, commentId)
    insert(userId, by, feedType, commentId.value, url)
  }

  def create(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val url = deepLinkService.getTweet(tweetId)
    val q = quote {
      infix"""
        insert into feeds (user_id, `by`, content_id, feed_type, url, unread, notified_at)
        select r.`by`, r.user_id, ${lift(tweetId)}, ${lift(FeedType.tweet.value)}, ${lift(url)}, true as unread, CURRENT_TIMESTAMP
        from relationships r, tweets f
        where f.id = ${lift(tweetId)}
        and r.user_id = ${lift(by)}
        and (
           (r.follow = true and (f.privacy_type in (0, 1)))
        or (r.is_friend = true and (f.privacy_type in (0, 1, 2)))
            )
        and r.muting = 0
        """.as[Action[Long]]
    }
    run(q).map(_ => ())
  }


  private def insert(userId: UserId, by: UserId, feedType: FeedType, contentId: Long, url: String): Future[FeedId] = {
    val notifiedAt = System.currentTimeMillis()
    val contentIdOpt: Option[Long] = Some(contentId)
    val q = quote {
      query[Feeds].insert(
        _.userId         -> lift(userId),
        _.by                -> lift(by),
        _.feedType  -> lift(feedType),
        _.contentId         -> lift(contentIdOpt),
        _.url               -> lift(url),
        _.unread            -> true,
        _.notifiedAt        -> lift(notifiedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def updateReadStatus(feedIds: Seq[FeedId], sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[Feeds]
        .filter(_.userId == lift(userId))
        .filter(n => liftQuery(feedIds).contains(n.id))
        .update(_.unread -> false)
    }
    run(q).map(_ => ())
  }

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           locales: Seq[Locale],
           sessionId: SessionId): Future[Seq[Feed]] = {

    val by = sessionId.userId
    val q = quote {
      (for {
        n <- query[Feeds]
          .filter(n => n.userId == lift(by))
          .filter(n => lift(since).forall(n.id < _))
          .filter(n => query[Blocks].filter(b => b.userId == lift(by) && b.by == n.by).isEmpty)
        a <- query[Users]
            .join(_.id == n.by)
        r <- query[Relationships]
            .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (n, a, r))
        .sortBy({ case (n, _, _) => n.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))

    }
    run(q).map(_.map({ case (n, a, r) =>
        val displayName = r.map(_.displayName).getOrElse(a.userName)
        val message = messageService.getPushMessage(n.feedType, locales, displayName)
        Feed(n, message, n.id.value)
      }))


  }

}
