package jp.smartreach.members.backend.core.infrastructure.daos

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import jp.smartreach.members.backend.core.infrastructure.identifiers.{GroupId, MemberId}
import jp.smartreach.members.backend.core.infrastructure.models.Groups

class GroupsDAO @Inject()(db: DatabaseService) {

  import db._



  def create(name: String, memberCount: Long, by: MemberId, createdAt: Long): Future[GroupId] = {
    val createdAt = System.currentTimeMillis()
    val q = quote {
      query[Groups]
        .insert(
          _.name            -> lift(name),
          _.memberCount     -> lift(memberCount),
          _.by              -> lift(by),
          _.createdAt       -> lift(createdAt)
      ).returning(_.id)
    }
    run(q)
  }

  def update(id: GroupId, name: String, memberCount: Long, by: MemberId, createdAt: Long): Future[Unit] = {
    val createdAt = System.currentTimeMillis()
    val q = quote {
      query[Groups]
        .filter(_.id == lift(id))
        .update(
          _.name            -> lift(name),
          _.memberCount     -> lift(memberCount),
          _.by              -> lift(by),
          _.createdAt       -> lift(createdAt)
      )
    }
    run(q).map(_ => ())
  }

  def exists(id: GroupId): Future[Boolean] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(id))
        .nonEmpty
    }
    run(q)
  }

  def delete(id: GroupId): Future[Unit] = {
    val q = quote {
      query[Groups]
        .filter(_.id == lift(id))
        .delete
    }
    run(q).map(_ => ())
  }
  
}
