package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class GroupsRepository {

  @Inject private var groupsDAO: GroupsDAO = _
  @Inject private var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject private var accountGroupsDAO: AccountGroupsDAO = _

  def create(name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[GroupId] = {
    for {
      id <- groupsDAO.create(name, byInvitationOnly, privacyType, authority, 1L, sessionId)
      _ <- accountGroupsDAO.create(sessionId.toAccountId, id)
    } yield (id)
  }

  def update(groupId: GroupId, name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[Unit] = {
    groupsDAO.find(groupId, sessionId).flatMap(_ match {
      case Some(g) =>
        if (g.directMessage) {
          Future.exception(CactaceaException(DirectMessageGroupCanNotUpdated))
        } else {
          groupsDAO.update(groupId, name, byInvitationOnly, privacyType, authority, sessionId).flatMap(_ =>
            if (g.privacyType != privacyType) {
              groupInvitationsDAO.deleteByGroupId(groupId).flatMap(_ => Future.Unit)
            } else {
              Future.Unit
            }
          )
        }
      case None =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

  def findAll(name: Option[String], byInvitation: Option[Boolean], privacyType: Option[GroupPrivacyType], since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Group]] = {
    groupsDAO.findAll(name, byInvitation, privacyType, since, offset, count, sessionId)
      .map(_.map(t => Group(t)))
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    groupsDAO.find(groupId, sessionId).flatMap(_ match {
      case Some(t) => Future.value(Group(t))
      case None => Future.exception(CactaceaException(GroupNotFound))
    })
  }

}