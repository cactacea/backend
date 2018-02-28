package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.{DeviceId, SessionId}
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class DevicesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

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
        .size
    }
    run(q).map(_ == 1)
  }

  def create(udid: String, info: Option[String], sessionId: SessionId): Future[DeviceId] = {
    for {
      id <- identifiesDAO.create().map(DeviceId(_))
      _ <- insert(id, udid, info, sessionId)
    } yield (id)
  }

  private def insert(id: DeviceId, udid: String, info: Option[String], sessionId: SessionId): Future[Long] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[Devices].insert(
        _.id          -> lift(id),
        _.accountId   -> lift(accountId),
        _.udid        -> lift(udid),
        _.userAgent        -> lift(info)
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
    run(r).map(_ == 1)
  }


}
