package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.IdentifyService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ActiveStatus, DeviceType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, DeviceId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class DevicesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def find(sessionId: SessionId): Future[List[Devices]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices]
        .filter(_.accountId == lift(accountId))
    }
    run(q)
  }

  def exist(sessionId: SessionId, udid: String): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices]
        .filter(_.udid == lift(udid))
        .filter(_.accountId == lift(accountId))
        .take(1)
        .size
    }
    run(q).map(_ == 1)
  }

  def findActiveStatus(accountId: AccountId): Future[Option[ActiveStatus]] = {
    val q = quote {
      query[Devices]
        .filter(_.accountId == lift(accountId))
        .map(_.activeStatus)
    }
    run(q).map(_.headOption)
  }

  def create(udid: String, deviceType: DeviceType, info: Option[String], sessionId: SessionId): Future[DeviceId] = {
    for {
      id <- identifyService.generate().map(DeviceId(_))
      _ <- insert(id, udid, deviceType, info, sessionId)
    } yield (id)
  }

  private def insert(id: DeviceId, udid: String, deviceType: DeviceType, info: Option[String], sessionId: SessionId): Future[Long] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices].insert(
        _.id            -> lift(id),
        _.accountId     -> lift(accountId),
        _.udid          -> lift(udid),
        _.deviceType    -> lift(deviceType),
        _.activeStatus  -> lift(ActiveStatus.inactive),
        _.userAgent     -> lift(info)
      )
    }
    run(q)
  }

  def delete(udid: String, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val r = quote {
      query[Devices]
        .filter(_.accountId   == lift(accountId))
        .filter(_.udid        == lift(udid))
        .delete
    }
    run(r).map(_ == 1)
  }

  def update(udid: String, deviceStatus: ActiveStatus, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val r = quote {
      query[Devices]
        .filter(_.accountId   == lift(accountId))
        .filter(_.udid        == lift(udid))
        .update(
          _.activeStatus   -> lift(deviceStatus)
        )
    }
    run(r).map(_ >= 1)
  }

  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val r = quote {
      query[Devices]
        .filter(_.accountId   == lift(accountId))
        .filter(_.udid        == lift(udid))
        .update(
          _.pushToken   -> lift(pushToken)
        )
    }
    run(r).map(_ >= 1)
  }


}
