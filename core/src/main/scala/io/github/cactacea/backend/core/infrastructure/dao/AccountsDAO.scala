package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{HashService, IdentifyService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.infrastructure.results.RelationshipBlocksCount

@Singleton
class AccountsDAO @Inject()(db: DatabaseService, hashService: HashService) {

  import db._

  @Inject private var timeService: TimeService = _
  @Inject private var blocksCountDAO: BlockCountDAO = _
  @Inject private var identifyService: IdentifyService = _

  def create(accountName: String, displayName: Option[String], password: String, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String]): Future[AccountId] = {
    for {
      id <- identifyService.generate().map(AccountId(_))
      _ <- insert(id, accountName, displayName, password, web, birthday, location, bio)
    } yield (id)
  }

  private def insert(id: AccountId, accountName: String, displayName: Option[String], password: String, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String]): Future[Long] = {
    val accountStatus = AccountStatusType.normally
    val hashedPassword = hashService.hash(password)
    val q = quote {
      query[Accounts].insert(
        _.id                    -> lift(id),
        _.accountName           -> lift(accountName),
        _.displayName           -> lift(displayName),
        _.password              -> lift(hashedPassword),
        _.accountStatus         -> lift(accountStatus),
        _.web                   -> lift(web),
        _.birthday              -> lift(birthday),
        _.location              -> lift(location),
        _.bio                   -> lift(bio)
      )
    }
    run(q)
  }

  def updateProfile(displayName: Option[String], web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String], sessionId: SessionId): Future[Unit] = {
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

  def updateProfileImageUri(profileImageUrl: Option[String], profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
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

  def exist(accountName: String): Future[Boolean] = {
    val q = quote {
      query[Accounts]
        .filter(_.accountName == lift(accountName))
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

  def exist(accountId: AccountId, sessionId: SessionId, ignoreBlockedUser: Boolean = true): Future[Boolean] = {
    if (ignoreBlockedUser) {
      val by = sessionId.toAccountId
      val status = AccountStatusType.normally
      val q = quote {
        query[Accounts]
          .filter(_.id == lift(accountId))
          .filter(_.accountStatus  == lift(status))
          .filter(u => query[Blocks]
            .filter(_.accountId    == u.id)
            .filter(_.by        == lift(by))
            .filter(b => b.blocked == true || b.beingBlocked == true)
            .isEmpty)
          .nonEmpty
      }
      run(q)
    } else {
      val status = AccountStatusType.normally
      val q = quote {
        query[Accounts]
          .filter(_.id == lift(accountId))
          .filter(_.accountStatus  == lift(status))
          .nonEmpty
      }
      run(q)
    }
  }

  def exist(accountIds: List[AccountId], sessionId: SessionId): Future[Boolean] = {

    val by = sessionId.toAccountId
    val status = AccountStatusType.normally
    val q = quote {
      query[Accounts]
        .filter(u => liftQuery(accountIds).contains(u.id))
        .filter(_.accountStatus  == lift(status))
        .filter(u => query[Blocks]
          .filter(_.accountId    == u.id)
          .filter(_.by        == lift(by))
          .filter(b => b.blocked == true || b.beingBlocked == true)
          .isEmpty)
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

  def find(sessionId: SessionId): Future[Option[Accounts]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
    }
    run(q).map(_.headOption)
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

  def find(accountId: AccountId, sessionId: SessionId): Future[Option[(Accounts, Option[Relationships])]] = {

    val by = sessionId.toAccountId
    val status = AccountStatusType.normally
    val q = quote {
      for {
        a <- query[Accounts]
          .filter(_.id      == lift(accountId))
          .filter(_.accountStatus  == lift(status))
          .filter(a => query[Blocks]
            .filter(_.accountId == a.id)
            .filter(_.by        == lift(by))
            .filter(b => b.blocked == true || b.beingBlocked == true)
            .isEmpty)
        r <- query[Relationships]
          .leftJoin(r => r.accountId == a.id && r.by == lift(by))
      } yield (a, r)
    }

    (for {
      accounts <- run(q)
      ids = accounts.map(_._1.id)
      blocksCount <- blocksCountDAO.findRelationshipBlocks(ids, sessionId)
    } yield (accounts, blocksCount))
      .map({ case (accounts, blocksCount) =>
        accounts.map({ t =>
          val a = t._1
          val r = t._2
          val b = blocksCount.filter(_.id == a.id).headOption.getOrElse(RelationshipBlocksCount(a.id, 0L, 0L, 0L))
          val displayName = r.map(_.editedDisplayName).getOrElse(a.displayName)
          val friendCount = a.friendCount - b.friendCount
          val followCount = a.followCount - b.followCount
          val followerCount = a.followerCount - b.followerCount
          val na = a.copy(displayName = displayName, friendCount = friendCount, followCount = followCount, followerCount = followerCount)
          (na, r)
        }).headOption
    })

  }

  def findAll(displayName: Option[String] = None, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[(Accounts, Option[Relationships])]] = {

    val by = sessionId.toAccountId

    val s = since.getOrElse(-1L)
    val c = count.getOrElse(20)
    val o = offset.getOrElse(0)

    val un = displayName.fold("") { _ + "%" }
    val status = AccountStatusType.normally

    val q2 = quote {
      query[Accounts]
        .filter({a => a.id !=  lift(by) && ((a.accountName like lift(un)) || a.displayName.exists(_ like lift(un)) || (lift(un) == "")) &&
          a.accountStatus  == lift(status) && (a.id < lift(s) || lift(s) == -1L) &&
          query[Blocks].filter(b => b.accountId == a.id && b.by == lift(by) && (b.blocked || b.beingBlocked)).isEmpty})
      .leftJoin(query[Relationships]).on({ case (a, r) => r.accountId == a.id && r.by == lift(by)})
      .sortBy({ case (a, _) => a.id})(Ord.descNullsLast)
      .drop(lift(o))
      .take(lift(c))
    }

    (for {
      accounts <- run(q2)
      ids = accounts.map(_._1.id)
      blocksCount <- blocksCountDAO.findRelationshipBlocks(ids, sessionId)
    } yield (accounts, blocksCount))
      .map({ case (accounts, blocksCount) =>
        accounts.map({ t =>
          val a = t._1
          val r = t._2
          val b = blocksCount.filter(_.id == a.id).headOption
          val friendCount = a.friendCount - b.map(_.friendCount).getOrElse(0L)
          val followCount = a.followCount - b.map(_.followCount).getOrElse(0L)
          val followerCount = a.followerCount - b.map(_.followerCount).getOrElse(0L)
          val displayName = r.map(_.editedDisplayName).getOrElse(a.displayName)
          val na = a.copy(displayName = displayName, friendCount = friendCount, followCount = followCount, followerCount = followerCount)
          (na, r)
        })
      })

  }

  def updateDisplayName(accountId: AccountId, displayName: Option[String], sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    _updateDisplayName(accountId, displayName, by).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        _insertUserName(accountId, displayName, by)
    })
  }

  private def _insertUserName(accountId: AccountId, userName: Option[String], by: AccountId): Future[Boolean] = {
    val q = quote {
      query[Relationships]
        .insert(
          _.accountId         -> lift(accountId),
          _.by                -> lift(by),
          _.editedDisplayName -> lift(userName)
        )
    }
    run(q).map(_ == 1)
  }

  private def _updateDisplayName(accountId: AccountId, displayName: Option[String], by: AccountId): Future[Boolean] = {
    val q = quote {
      query[Relationships]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .update(
          _.editedDisplayName   -> lift(displayName)
        )
    }
    run(q).map(_ == 1)
  }


  def signOut(sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val signedOutAt: Option[Long] = Some(timeService.nanoTime())
    val q = quote {
      query[Accounts]
        .filter(_.id == lift(accountId))
        .update(
          _.signedOutAt   -> lift(signedOutAt)
        )
    }
    run(q).map(_ == 1)
  }

}


