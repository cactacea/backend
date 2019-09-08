package jp.smartreach.members.backend.core.infrastructure.daos

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.smartreach.members.backend.core.domain.enums.CommunicationType
import jp.smartreach.members.backend.core.infrastructure.identifiers.MemberId
import jp.smartreach.members.backend.core.infrastructure.models.Members

class MembersDAO @Inject()(db: DatabaseService) {

  import db._


  def create(communicationType: CommunicationType,
             userId: Option[UserId], email: Option[String], phoneNo: Option[String]): Future[MemberId] = {

    val registeredAt = System.currentTimeMillis()
    val q = quote {
      query[Members]
        .insert(
          _.communicationType   -> lift(communicationType),
          _.userId              -> lift(userId),
          _.email               -> lift(email),
          _.phoneNo             -> lift(phoneNo),
          _.registeredAt        -> lift(registeredAt)
        ).returning(_.id)
    }
    run(q)
  }

  def update(id: MemberId, communicationType: CommunicationType,
             userId: Option[UserId], email: Option[String], phoneNo: Option[String]): Future[Unit] = {

    val registeredAt = System.currentTimeMillis()
    val q = quote {
      query[Members]
        .filter(_.id == lift(id))
        .update(
          _.communicationType   -> lift(communicationType),
          _.userId              -> lift(userId),
          _.email               -> lift(email),
          _.phoneNo             -> lift(phoneNo),
          _.registeredAt        -> lift(registeredAt)
      )
    }
    run(q).map(_ => ())
  }

  def update(id: MemberId, name: Option[String], address: Option[String], city: Option[String],
             state: Option[String], zip: Option[String], tel: Option[String]): Future[Unit] = {

    val registeredAt = System.currentTimeMillis()
    val q = quote {
      query[Members]
        .filter(_.id == lift(id))
        .update(
          _.name    -> lift(name),
          _.address -> lift(address),
          _.city    -> lift(city),
          _.state   -> lift(state),
          _.zip     -> lift(zip),
          _.tel     -> lift(tel)
        )
    }
    run(q).map(_ => ())
  }




  def exists(id: MemberId): Future[Boolean] = {
    val q = quote {
      query[Members]
        .filter(_.id == lift(id))
        .nonEmpty
    }
    run(q)
  }

  def delete(id: MemberId): Future[Unit] = {
    val q = quote {
      query[Members]
        .filter(_.id == lift(id))
        .delete
    }
    run(q).map(_ => ())
  }
}



