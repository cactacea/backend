package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, SessionId}

@Singleton
class GroupsRepository @Inject()(
                                  accountGroupsDAO: AccountGroupsDAO,
                                  groupsDAO: GroupsDAO,
                                  groupAuthorityDAO: GroupAuthorityDAO
                                ) {

  def create(name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[GroupId] = {
    for {
      id <- groupsDAO.create(name, byInvitationOnly, privacyType, authority, sessionId)
      _ <- accountGroupsDAO.create(sessionId.toAccountId, id)
    } yield (id)
  }

  def update(groupId: GroupId,
             name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    for {
      _ <- groupAuthorityDAO.validateUpdateAuthority(groupId, sessionId)
      _ <- groupsDAO.update(groupId, name, byInvitationOnly, privacyType, authority, sessionId)
    } yield (Unit)
  }

  def find(name: Option[String],
           byInvitation: Option[Boolean],
           privacyType: Option[GroupPrivacyType],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[List[Group]] = {

    groupsDAO.find(
      name,
      byInvitation,
      privacyType,
      since,
      offset,
      count,
      sessionId)
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    for {
      r <- groupsDAO.validateFind(groupId, sessionId)
    } yield (r)
  }

}