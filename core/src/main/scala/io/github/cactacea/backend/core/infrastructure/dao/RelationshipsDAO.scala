package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.Relationships

@Singleton
class RelationshipsDAO @Inject()(db: DatabaseService) {

  import db._

  def createFollow(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .insert(
          _.userId       -> lift(userId),
          _.by              -> lift(by),
          _.follow       -> true
        ).onConflictUpdate((t, _) =>
        t.follow -> true)
    }
    run(q).map(_ => ())
  }

  def deleteFollow(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .filter(_.userId     == lift(userId))
        .filter(_.by            == lift(by))
        .update(
          _.follow          -> false
        )
    }
    run(q).map(_ => ())
  }

  def updateFollowBlockCount(userId: UserId, count: Long, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      infix"""
            insert into relationships (user_id, `by`, follow_block_count)
            select user_id, `by`, cnt from (select ${lift(userId)} as user_id, `by`, ${lift(count)} as cnt from blocks where user_id = ${lift(by)}) t
            on duplicate key update follow_block_count = follow_block_count + ${lift(count)};
          """.as[Action[Long]]
    }
    run(q).map(_ => ())
  }

  def createFollower(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .insert(
          _.userId         -> lift(userId),
          _.by                -> lift(by),
          _.isFollower          -> true
        ).onConflictUpdate((t, _) =>
        t.isFollower -> true)
    }
    run(q).map(_ => ())
  }

  def deleteFollower(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .update(
          _.isFollower        -> false
        )
    }
    run(q).map(_ => ())
  }

  def updateFollowerBlockCount(userId: UserId, plus: Long, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      infix"""
             insert into relationships (user_id, `by`, follower_block_count)
             select user_id, `by`, cnt from (select ${lift(userId)} as user_id, `by`, ${lift(plus)} as cnt from blocks where user_id = ${lift(by)}) t
             on duplicate key update follower_block_count = follower_block_count + ${lift(plus)};
          """.as[Action[Long]]
    }
    run(q).map(_ => ())
  }

  def updateFriendBlockCount(userId: UserId, plus: Long, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      infix"""
             insert into relationships (user_id, `by`, friend_block_count)
             select user_id, `by`, cnt from (select ${lift(userId)} as user_id, `by`, ${lift(plus)} as cnt from blocks where user_id = ${lift(by)}) t
             on duplicate key update friend_block_count = friend_block_count + ${lift(plus)};
          """.as[Action[Long]]
    }
    run(q).map(_ => ())
  }

  def createFriend(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .insert(
          _.userId         -> lift(userId),
          _.by                -> lift(by),
          _.isFriend            -> true
        ).onConflictUpdate((t, _) => t.isFriend -> true)
    }
    run(q).map(_ => ())
  }

  def deleteFriend(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .update(
          _.isFriend          -> lift(false)
        )
    }
    run(q).map(_ => ())
  }

  def createMute(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .insert(
          _.userId         -> lift(userId),
          _.by                -> lift(by),
          _.muting            -> true
        ).onConflictUpdate((t, _) => t.muting -> true)
    }
    run(q).map(_ => ())
  }

  def deleteMute(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .update(
          _.muting          -> lift(false)
        )
    }
    run(q).map(_ => ())
  }

  def createRequestInProgress(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .insert(
          _.userId    -> lift(userId),
          _.by           -> lift(by),
          _.friendRequestInProgress   -> true
        ).onConflictUpdate((t, _) => t.friendRequestInProgress -> true)
    }
    run(q).map(_ => ())
  }

  def deleteRequestInProgress(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .filter(_.userId     == lift(userId))
        .filter(_.by            == lift(by))
        .update(
          _.friendRequestInProgress          -> false
        )
    }
    run(q).map(_ => ())
  }

}
