package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.responses.GroupCreated
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.domain.repositories._
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupsService @Inject()(db: DatabaseService) {

  @Inject var groupsRepository: GroupsRepository = _
  @Inject var groupAccountsRepository: GroupAccountsRepository = _
  @Inject var reportsRepository: ReportsRepository = _
  @Inject var messagesRepository: MessagesRepository = _

  def create(name: String, byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[GroupCreated] = {
    db.transaction {
      groupsRepository.create(
        Some(name),
        byInvitationOnly,
        privacyType,
        authority,
        sessionId
      ).map(GroupCreated(_))
    }
  }

  def update(groupId: GroupId, name: String, byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupsRepository.update(
        groupId,
        Some(name),
        byInvitationOnly,
        privacyType,
        authority,
        sessionId
      )
    }
  }

  def find(name: Option[String], byInvitation: Option[Boolean], privacyType: Option[GroupPrivacyType], since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Group]] = {
    groupsRepository.findAll(name, byInvitation, privacyType, since, offset, count, sessionId)
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    groupsRepository.find(groupId, sessionId)
  }

  def report(groupId: GroupId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      reportsRepository.createGroupReport(groupId, reportType, sessionId)
    }
  }

}
