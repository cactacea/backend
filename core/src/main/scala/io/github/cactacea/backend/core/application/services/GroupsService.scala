package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, SessionId}

class GroupsService @Inject()(
                               databaseService: DatabaseService,
                               groupsRepository: GroupsRepository
                             ) {

  import databaseService._

  def create(name: String, byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[GroupId] = {
    transaction {
      groupsRepository.create(Some(name), byInvitationOnly, privacyType, authority, sessionId)
    }
  }

  def update(groupId: GroupId,
             name: String,
             invitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    transaction {
      groupsRepository.update(groupId, Some(name), invitationOnly, privacyType, authority, sessionId)
    }
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    groupsRepository.find(groupId, sessionId)
  }

  def report(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      groupsRepository.report(groupId, reportType, reportContent, sessionId)
    }
  }

}
