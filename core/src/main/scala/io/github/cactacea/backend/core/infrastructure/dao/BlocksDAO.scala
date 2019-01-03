package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Blocks, Relationships}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyBlocked, AccountNotBlocked}

@Singleton
class BlocksDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val blockedAt = System.currentTimeMillis()
    val q = quote {
      query[Blocks]
        .insert(
          _.accountId     -> lift(accountId),
          _.by            -> lift(by),
          _.blockedAt     -> lift(blockedAt)
        ).returning(_.id)
    }
    run(q).map(_ => Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[Blocks]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {

    val by = sessionId.toAccountId
    val status = AccountStatusType.normally

    val q = quote {
      query[Blocks]
        .filter(b => b.by == lift(by))
        .filter(b => (lift(since).forall(b.id < _)) )
        .join(query[Accounts]).on((b, a) => a.id == b.accountId && a.accountStatus == lift(status))
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by) })
        .map({ case ((b, a), r) => (a, r, b)})
        .sortBy({ case (_, _, b) => b.id })(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, b) => Account(a, r, b, b.id.value)}))

  }


  def validateExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotBlocked))
    })
  }

  def validateNotExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyBlocked))
      case false =>
        Future.Unit
    })
  }

}
