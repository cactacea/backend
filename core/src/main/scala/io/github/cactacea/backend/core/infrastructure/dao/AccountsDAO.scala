package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountName: String): Future[AccountId] = {

    val accountStatus = AccountStatusType.normally
    val q = quote {
      query[Accounts].insert(
        _.accountName           -> lift(accountName),
        _.displayName           -> lift(accountName),
        _.accountStatus         -> lift(accountStatus)
      ).returning(_.id)
    }
    run(q)
  }

  def create(accountName: String, displayName: String): Future[AccountId] = {

    val accountStatus = AccountStatusType.normally
    val q = quote {
      query[Accounts].insert(
        _.accountName           -> lift(accountName),
        _.displayName           -> lift(displayName),
        _.accountStatus         -> lift(accountStatus)
      ).returning(_.id)
    }
    run(q)
  }

  def exists(accountName: String): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.accountName == lift(accountName))
        .nonEmpty
    }
    run(q)
  }

  def exists(accountName: String, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.accountName == lift(accountName))
        .filter(_.id != lift(by))
        .nonEmpty
    }
    run(q)
  }

  def exists(accountId: AccountId): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .nonEmpty
    }
    run(q)
  }

  def exists(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .filter(u => query[Blocks].filter(b => b.accountId == lift(by) && b.by == u.id).isEmpty)
        .nonEmpty
    }
    run(q)
  }

  def exists(accountIds: List[AccountId], sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(u => liftQuery(accountIds).contains(u.id))
        .filter(u => query[Blocks].filter(b => b.accountId == lift(by) && b.by == u.id).isEmpty)
        .size
    }
    run(q).map(_ == accountIds.size)
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
          .filter(a => query[Blocks].filter(b => b.accountId == lift(by) && b.by == a.id).isEmpty)
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
          .filter(a => query[Blocks].filter(b => b.accountId == lift(by) && b.by == a.id).isEmpty)
        r <- query[Relationships]
            .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r))
        .sortBy({ case (a, _) => a.accountName})(Ord.asc)
        .drop(lift(offset))
        .take(lift(count))
    }

    run(q).map(_.map({ case (a, r) => Account(a, r) }))

  }

  def find(providerId: String, providerKey: String): Future[Option[Account]] = {
    val q = quote {
      for {
        au <- query[Authentications]
          .filter(_.providerId == lift(providerId))
          .filter(_.providerKey == lift(providerKey))
        a <- query[Accounts]
          .filter(_.id == au.accountId)
      } yield (a)
    }
    run(q).map(_.headOption.map(Account(_)))
  }

  def signOut(sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val signedOutAt = Option(System.currentTimeMillis())
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
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
    run(q).map(_ => ())
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
    run(q).map(_ => ())
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
    run(q).map(_ => ())
  }

  def updateAccountStatus(accountStatus: AccountStatusType, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          _.accountStatus   -> lift(accountStatus)
        )
    }
    run(q).map(_ => ())
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
    run(q).map(_ => ())
  }

}


