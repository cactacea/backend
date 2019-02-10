package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao.GroupsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.GroupNotFound

@Singleton
class GroupsValidator @Inject()(groupsDAO: GroupsDAO) {

  def exist(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    groupsDAO.exist(groupId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    groupsDAO.find(groupId, sessionId).flatMap(_ match {
      case Some(t) => Future.value(t)
      case None => Future.exception(CactaceaException(GroupNotFound))
    })
  }

}

