package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, SessionId}

@Singleton
class GroupsService @Inject()(
                               db: DatabaseService,
                               groupsRepository: GroupsRepository,
                               reportsRepository: ReportsRepository,
                               listenerService: ListenerService
                             ) {

  def create(name: String, byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[GroupId] = {
    for {
      id <- db.transaction(groupsRepository.create(Some(name), byInvitationOnly, privacyType, authority, sessionId))
      _ <- listenerService.groupCreated(id, Some(name), byInvitationOnly, privacyType, authority, sessionId)
    } yield (id)
  }

  def update(groupId: GroupId,
             name: String,
             invitationOnly: Boolean,
             privacyType: GroupPrivacyType,
             authority: GroupAuthorityType,
             sessionId: SessionId): Future[Unit] = {

    for {
      _ <- db.transaction(groupsRepository.update(groupId, Some(name), invitationOnly, privacyType, authority, sessionId))
      _ <- listenerService.groupUpdated(groupId, Some(name), invitationOnly, privacyType, authority, sessionId)
    } yield (Unit)

  }

  def find(name: Option[String],
           byInvitation: Option[Boolean],
           privacyType: Option[GroupPrivacyType],
           since: Option[Long],
           offset: Int,
           count: Int, sessionId: SessionId): Future[List[Group]] = {

    groupsRepository.find(name, byInvitation, privacyType, since, offset, count, sessionId)
  }

  def find(groupId: GroupId, sessionId: SessionId): Future[Group] = {
    groupsRepository.find(groupId, sessionId)
  }

  def report(groupId: GroupId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(reportsRepository.createGroupReport(groupId, reportType, reportContent, sessionId))
      _ <- listenerService.groupReported(groupId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

}
