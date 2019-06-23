package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.HashService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.domain.models.{Account}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountsDAO @Inject()(
                             db: DatabaseService,
                             hashService: HashService
                           ) {

  import db._

  def create(accountName: String,
             password: String): Future[AccountId] = {

    val accountStatus = AccountStatusType.normally
    val hashedPassword = hashService.hash(password)
    val q = quote {
      query[Accounts].insert(
        _.accountName           -> lift(accountName),
        _.displayName           -> lift(accountName),
        _.password              -> lift(hashedPassword),
        _.accountStatus         -> lift(accountStatus)
      ).returning(_.id)
    }
    run(q)
  }

  def updateProfile(displayName: String,
                    web: Option[String],
                    birthday: Option[Long],
                    location: Option[String],
                    bio: Option[String],
                    sessionId: SessionId): Future[Unit] = {

    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          _.displayName -> lift(displayName),
          _.web         -> lift(web),
          _.birthday    -> lift(birthday),
          _.location    -> lift(location),
          _.bio         -> lift(bio)
        )
    }
    run(q).map(_ => Unit)
  }

  def updateProfileImageUrl(profileImageUrl: Option[String], profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          _.profileImage    -> lift(profileImage),
          _.profileImageUrl -> lift(profileImageUrl)
        )
    }
    run(q).map(_ => Unit)
  }

  def updateAccountName(accountName: String, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          _.accountName   -> lift(accountName)
        )
    }
    run(q).map(_ => Unit)
  }

  def updatePassword(oldPassword: String, newPassword: String, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val hashedNewPassword = hashService.hash(newPassword)
    val hashedOldPassword = hashService.hash(oldPassword)
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .filter(_.password == lift(hashedOldPassword))
        .update(
          _.password -> lift(hashedNewPassword)
        )
    }
    run(q).map(_ => Unit)
  }

  def updatePassword(newPassword: String, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val hashedNewPassword = hashService.hash(newPassword)
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          _.password -> lift(hashedNewPassword)
        )
    }
    run(q).map(_ => Unit)
  }

  def updateDisplayName(accountId: AccountId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.displayName       -> lift(displayName)
        ).onConflictUpdate((t, _) => t.displayName -> lift(displayName))
    }
    run(q).map(_ => Unit)
  }


  def find(accountName: String, password: String): Future[Option[Accounts]] = {
    val hashedPassword = hashService.hash(password)
    val q = quote {
      query[Accounts]
        .filter(_.accountName == lift(accountName))
        .filter(_.password    == lift(hashedPassword))
    }
    run(q).map(_.headOption)
  }

  def exist(accountName: String): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.accountName == lift(accountName))
        .nonEmpty
    }
    run(q)
  }

  def exist(accountName: String, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.accountName == lift(accountName))
        .filter(_.id != lift(by))
        .nonEmpty
    }
    run(q)
  }

  def exist(accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .nonEmpty
    }
    run(q)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val status = AccountStatusType.normally
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .filter(_.accountStatus  == lift(status))
        .filter(u => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == u.id) || (b.accountId == u.id && b.by == lift(by))
        ).isEmpty)
        .nonEmpty
    }
    run(q)
  }

  def exist(accountIds: List[AccountId], sessionId: SessionId): Future[Boolean] = {

    val by = sessionId.toAccountId
    val status = AccountStatusType.normally
    val q = quote {
      query[Accounts]
        .filter(u => liftQuery(accountIds).contains(u.id))
        .filter(_.accountStatus  == lift(status))
        .filter(u => query[Blocks].filter(b =>
          (b.accountId == lift(by) && b.by == u.id) || b.accountId == u.id && b.by == lift(by)
        ).isEmpty)
        .size
    }
    run(q).map(_ == accountIds.size)
  }

  def findStatus(sessionId: SessionId): Future[Option[(AccountStatusType, Option[Long])]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .map(a => (a.accountStatus, a.signedOutAt))
    }
    run(q).map(_.headOption)
  }

  def find(sessionId: SessionId): Future[Option[(Account)]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
    }
    run(q).map(_.headOption.map(Account(_)))
  }


  def find(accountId: AccountId, sessionId: SessionId): Future[Option[Account]] = {

    val by = sessionId.toAccountId
    val status = AccountStatusType.normally
    val q = quote {
      (for {
        a <- query[Accounts]
          .filter(_.id              == lift(accountId))
          .filter(_.accountStatus   == lift(status))
          .filter(a => query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == a.id) || (b.accountId == a.id && b.by == lift(by))
          ).isEmpty)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r))
    }
    run(q).map(_.map({ case (a, r) => Account(a, r)}).headOption)

  }

  def find(accountName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      n <- findAccountName(since)
      r <- findSortByAccountName(accountName, n, offset, count, sessionId)
    } yield (r)
  }

  private def findAccountName(since: Option[Long]): Future[Option[String]] = {
    since match {
      case Some(id) =>
        val accountId = AccountId(id)
        val q = quote {
          query[Accounts]
            .filter(_.id == lift(accountId))
            .map(_.accountName)
        }
        run(q).map(_.headOption)
      case None =>
        Future.None
    }
  }

  private def findSortByAccountName(accountName: Option[String],
                                    sinceAccountName: Option[String],
                                    offset: Int,
                                    count: Int,
                                    sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId

    val q = quote {
      (for {
        a <- query[Accounts]
          .filter({a => a.id !=  lift(by)})
          .filter(a => a.accountStatus == lift(AccountStatusType.normally))
          .filter(a => lift(accountName.map(_ + "%")).forall(a.accountName like _))
          .filter(a => lift(sinceAccountName).forall(a.accountName gt _))
          .filter(a => query[Blocks].filter(b =>
            (b.accountId == lift(by) && b.by == a.id) || (b.accountId == a.id && b.by == lift(by))
          ).isEmpty)
        r <- query[Relationships]
            .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r))
        .sortBy({ case (a, _) => a.accountName})(Ord.asc)
        .drop(lift(offset))
        .take(lift(count))
    }

    run(q).map(_.map({ case (a, r) => Account(a, r) }))

  }



  def signOut(sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val signedOutAt: Option[Long] = Some(System.currentTimeMillis())
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          _.signedOutAt   -> lift(signedOutAt)
        )
    }
    run(q).map(_ => Unit)
  }


}


