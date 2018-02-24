package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.domain.factories.GroupFactory
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, SessionId}
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

@Singleton
class GroupsRepository {

  @Inject var groupsDAO: GroupsDAO = _
  @Inject var groupInvitesDAO: GroupInvitesDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _

  def create(name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[GroupId] = {
    for {
      id <- groupsDAO.create(name, byInvitationOnly, privacyType, authority, 1L, sessionId)
      _ <- accountGroupsDAO.create(sessionId.toAccountId, id)
    } yield (id)
  }

  def update(groupId: GroupId, name: Option[String], byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[Unit] = {
    groupsDAO.find(groupId, sessionId).flatMap(_ match {
      case Some(g) =>
        if (g.isDirectMessage) {
          Future.exception(CactaceaException(DirectMessageGroupCanNotUpdated))
        } else {
          groupsDAO.update(groupId, name, byInvitationOnly, privacyType, authority, sessionId).flatMap(_ =>
            if (g.privacyType != privacyType.toValue) {
              groupInvitesDAO.deleteByGroupId(groupId).flatMap(_ => Future.Unit)
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
      .map(_.map(t => GroupFactory.create(t)))
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    groupsDAO.find(groupId, sessionId).flatMap(_ match {
      case Some(t) => Future.value(GroupFactory.create(t))
      case None => Future.exception(CactaceaException(GroupNotFound))
    })
  }

}