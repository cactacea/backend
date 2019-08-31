package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, GroupAuthorityValidator, GroupsValidator}


class GroupsRepository @Inject()(
                                  accountGroupsValidator: AccountGroupsValidator,
                                  groupsValidator: GroupsValidator,
                                  groupAuthorityValidator: GroupAuthorityValidator,
                                  accountGroupsDAO: AccountGroupsDAO,
                                  groupsDAO: GroupsDAO,
                                  groupReportsDAO: GroupReportsDAO
                                ) {

  def create(name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[GroupId] = {
    for {
      id <- groupsDAO.create(name, byInvitationOnly, privacyType, authority, sessionId)
      _ <- accountGroupsDAO.create(sessionId.toAccountId, id, sessionId)
    } yield (id)
  }

  def update(groupId: GroupId,
             name: Option[String],
             byInvitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    for {
      _ <- accountGroupsValidator.mustJoined(sessionId.toAccountId, groupId)
      _ <- groupAuthorityValidator.hasUpdateAuthority(groupId, sessionId)
      _ <- groupsDAO.update(groupId, name, byInvitationOnly, privacyType, authority, sessionId)
    } yield (())
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    groupsValidator.mustFind(groupId, sessionId)
  }

  def report(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- groupsValidator.mustExist(groupId, sessionId)
      _ <- groupReportsDAO.create(groupId, reportType, reportContent, sessionId)
    } yield (())
  }

}