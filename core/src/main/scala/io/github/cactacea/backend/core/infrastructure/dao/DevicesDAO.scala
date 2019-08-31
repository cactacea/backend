package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, DeviceType}
import io.github.cactacea.backend.core.domain.models.AccountStatus
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class DevicesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(udid: String, pushToken: Option[String], deviceType: DeviceType, userAgent: Option[String], sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val registeredAt = System.currentTimeMillis()
    val q = quote {
      query[Devices].insert(
        _.accountId     -> lift(accountId),
        _.udid          -> lift(udid),
        _.deviceType    -> lift(deviceType),
        _.pushToken     -> lift(pushToken),
        _.activeStatus  -> lift(ActiveStatusType.active),
        _.userAgent     -> lift(userAgent),
        _.registeredAt  -> lift(registeredAt)
      ).onConflictIgnore
    }
    run(q).map(_ => ())
  }

  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices]
        .filter(_.accountId   == lift(accountId))
        .filter(_.udid        == lift(udid))
        .update(
          _.activeStatus    -> lift(ActiveStatusType.active),
          _.pushToken       -> lift(pushToken)
        )
    }
    run(q).map(_ => ())
  }


  def delete(udid: String, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices]
        .filter(_.accountId   == lift(accountId))
        .filter(_.udid        == lift(udid))
        .delete
    }
    run(q).map(_ => ())
  }

  def exists(udid: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices]
        .filter(_.accountId   == lift(accountId))
        .filter(_.udid        == lift(udid))
        .nonEmpty
    }
    run(q)
  }

  def findActiveStatus(accountId: AccountId): Future[AccountStatus] = {
    val q = quote {
      query[Devices]
        .filter(_.accountId == lift(accountId))
        .filter(_.activeStatus == lift(ActiveStatusType.active))
        .take(1)
        .size
    }
    run(q).flatMap(_ match {
      case 0 =>
        Future.value(AccountStatus(accountId, ActiveStatusType.inactive))
      case _ =>
        Future.value(AccountStatus(accountId, ActiveStatusType.active))
    })
  }

}
