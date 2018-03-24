package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, ReportType}
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, SessionId}

@Singleton
class GroupsService {

  @Inject private var db: DatabaseService = _
  @Inject private var groupsRepository: GroupsRepository = _
  @Inject private var reportsRepository: ReportsRepository = _
  @Inject private var injectionService: InjectionService = _

  def create(name: String, byInvitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[GroupId] = {
    db.transaction {
      for {
        id <- groupsRepository.create(Some(name), byInvitationOnly, privacyType, authority, sessionId)
        _ <- injectionService.groupCreated(id, sessionId)
      } yield (id)
    }
  }

  def update(groupId: GroupId, name: String, invitationOnly: Boolean, privacyType: GroupPrivacyType, authority: GroupAuthorityType, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- groupsRepository.update(groupId, Some(name), invitationOnly, privacyType, authority, sessionId)
        _ <- injectionService.groupUpdated(groupId, sessionId)
      } yield (r)
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
      for {
        r <- reportsRepository.createGroupReport(groupId, reportType, sessionId)
        _ <- injectionService.groupReported(groupId, reportType, sessionId)
      } yield (r)
    }
  }

}
