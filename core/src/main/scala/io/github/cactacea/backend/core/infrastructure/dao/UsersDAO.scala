package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class UsersDAO @Inject()(db: DatabaseService) {

  import db._

  def create(userName: String): Future[UserId] = {

    val userStatus = UserStatusType.normally
    val q = quote {
      query[Users].insert(
        _.userName           -> lift(userName),
        _.displayName           -> lift(userName),
        _.userStatus         -> lift(userStatus)
      ).returning(_.id)
    }
    run(q)
  }

  def create(userName: String, displayName: String): Future[UserId] = {

    val userStatus = UserStatusType.normally
    val q = quote {
      query[Users].insert(
        _.userName           -> lift(userName),
        _.displayName           -> lift(displayName),
        _.userStatus         -> lift(userStatus)
      ).returning(_.id)
    }
    run(q)
  }

  def exists(userName: String): Future[Boolean] = {
    val q = quote {
      query[Users]
        .filter(_.userName == lift(userName))
        .nonEmpty
    }
    run(q)
  }

  def exists(userName: String, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.userName == lift(userName))
        .filter(_.id != lift(by))
        .nonEmpty
    }
    run(q)
  }

  def exists(userId: UserId): Future[Boolean] = {
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .nonEmpty
    }
    run(q)
  }

  def exists(userId: UserId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .filter(u => query[Blocks].filter(b => b.userId == lift(by) && b.by == u.id).isEmpty)
        .nonEmpty
    }
    run(q)
  }

  def exists(userIds: Seq[UserId], sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Users]
        .filter(u => liftQuery(userIds).contains(u.id))
        .filter(u => query[Blocks].filter(b => b.userId == lift(by) && b.by == u.id).isEmpty)
        .size
    }
    run(q).map(_ == userIds.size)
  }


  def find(sessionId: SessionId): Future[Option[(User)]] = {
    val userId = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
    }
    run(q).map(_.headOption.map(User(_)))
  }

  def find(userId: UserId, sessionId: SessionId): Future[Option[User]] = {

    val by = sessionId.userId
    val status = UserStatusType.normally
    val q = quote {
      (for {
        a <- query[Users]
          .filter(_.id              == lift(userId))
          .filter(_.userStatus   == lift(status))
          .filter(a => query[Blocks].filter(b => b.userId == lift(by) && b.by == a.id).isEmpty)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (a, r))
    }
    run(q).map(_.map({ case (a, r) => User(a, r)}).headOption)

  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[User]] = {
    for {
      n <- findUserName(since)
      r <- findSortByUserName(userName, n, offset, count, sessionId)
    } yield (r)
  }

  private def findUserName(since: Option[Long]): Future[Option[String]] = {
    since match {
      case Some(id) =>
        val userId = UserId(id)
        val q = quote {
          query[Users]
            .filter(_.id == lift(userId))
            .map(_.userName)
        }
        run(q).map(_.headOption)
      case None =>
        Future.None
    }
  }

  private def findSortByUserName(userName: Option[String],
                                    sinceUserName: Option[String],
                                    offset: Int,
                                    count: Int,
                                    sessionId: SessionId): Future[Seq[User]] = {

    val by = sessionId.userId

    val q = quote {
      (for {
        a <- query[Users]
          .filter({a => a.id !=  lift(by)})
          .filter(a => a.userStatus == lift(UserStatusType.normally))
          .filter(a => lift(userName.map(_ + "%")).forall(a.userName like _))
          .filter(a => lift(sinceUserName).forall(a.userName gt _))
          .filter(a => query[Blocks].filter(b => b.userId == lift(by) && b.by == a.id).isEmpty)
        r <- query[Relationships]
            .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (a, r))
        .sortBy({ case (a, _) => a.userName})(Ord.asc)
        .drop(lift(offset))
        .take(lift(count))
    }

    run(q).map(_.map({ case (a, r) => User(a, r) }))

  }

  def find(providerId: String, providerKey: String): Future[Option[User]] = {
    val q = quote {
      for {
        au <- query[UserAuthentications]
          .filter(_.providerId == lift(providerId))
          .filter(_.providerKey == lift(providerKey))
        a <- query[Users]
          .filter(_.id == au.userId)
      } yield (a)
    }
    run(q).map(_.headOption.map(User(_)))
  }

  def signOut(sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val signedOutAt = Option(System.currentTimeMillis())
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .update(
          _.signedOutAt   -> lift(signedOutAt)
        )
    }
    run(q).map(_ => ())
  }

  def updateProfile(displayName: String,
                    web: Option[String],
                    birthday: Option[Long],
                    location: Option[String],
                    bio: Option[String],
                    sessionId: SessionId): Future[Unit] = {

    val userId = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .update(
          _.displayName -> lift(displayName),
          _.web         -> lift(web),
          _.birthday    -> lift(birthday),
          _.location    -> lift(location),
          _.bio         -> lift(bio)
        )
    }
    run(q).map(_ => ())
  }

  def updateProfileImageUrl(profileImageUrl: Option[String], profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .update(
          _.profileImage    -> lift(profileImage),
          _.profileImageUrl -> lift(profileImageUrl)
        )
    }
    run(q).map(_ => ())
  }

  def updateUserName(userName: String, sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .update(
          _.userName   -> lift(userName)
        )
    }
    run(q).map(_ => ())
  }

  def updateUserStatus(userStatus: UserStatusType, sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .update(
          _.userStatus   -> lift(userStatus)
        )
    }
    run(q).map(_ => ())
  }

  def updateDisplayName(userId: UserId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Relationships]
        .insert(
          _.userId         -> lift(userId),
          _.by                -> lift(by),
          _.displayName       -> lift(displayName)
        ).onConflictUpdate((t, _) => t.displayName -> lift(displayName))
    }
    run(q).map(_ => ())
  }

}


