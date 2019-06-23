package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{GroupAuthorityValidator, GroupsValidator}

@Singleton
class GroupsRepository @Inject()(
                                  groupsValidator: GroupsValidator,
                                  groupAuthorityValidator: GroupAuthorityValidator,
                                  accountGroupsDAO: AccountGroupsDAO,
                                  groupsDAO: GroupsDAO
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
      _ <- groupAuthorityValidator.hasUpdateAuthority(groupId, sessionId)
      _ <- groupsDAO.update(groupId, name, byInvitationOnly, privacyType, authority, sessionId)
    } yield (())
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
      r <- groupsValidator.find(groupId, sessionId)
    } yield (r)
  }

}