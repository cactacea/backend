package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Users, Blocks, Relationships}

@Singleton
class BlocksDAO @Inject()(db: DatabaseService) {

  import db._

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val blockedAt = System.currentTimeMillis()
    val q = quote {
      query[Blocks]
        .insert(
          _.userId     -> lift(userId),
          _.by            -> lift(by),
          _.blockedAt     -> lift(blockedAt)
        ).returning(_.id)
    }
    run(q).map(_ => ())
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Blocks]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(userId: UserId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Blocks]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[User]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        b <- query[Blocks]
          .filter(_.by == lift(by))
          .filter(b => (lift(since).forall(b.id < _)))
        a <- query[Users]
          .join(_.id == b.userId)
          .filter(a => lift(userName.map(_ + "%")).forall(a.userName like _))
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (a, r, b))
        .sortBy({ case (_, _, b) => b.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, b) => User(a, r, b)}))
  }

}
