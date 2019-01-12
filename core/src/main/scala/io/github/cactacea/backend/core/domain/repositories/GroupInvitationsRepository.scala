package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, MessageType, PushNotificationType}
import io.github.cactacea.backend.core.domain.models.{GroupInvitation, PushNotification}
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class GroupInvitationsRepository @Inject()(
                                            accountsDAO: AccountsDAO,
                                            accountGroupsDAO: AccountGroupsDAO,
                                            groupAuthorityDAO: GroupAuthorityDAO,
                                            groupInvitationsDAO: GroupInvitationsDAO,
                                            messagesDAO: MessagesDAO,
                                            deepLinkService: DeepLinkService,
                                          ) {

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountGroupsDAO.validateNotExist(accountId, groupId)
      _ <- groupInvitationsDAO.validateNotExist(accountId, groupId)
      _ <- groupAuthorityDAO.validateInviteMembersAuthority(accountId, groupId, sessionId)
      id <- groupInvitationsDAO.create(accountId, groupId, sessionId)
    } yield (id)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsDAO.find(since, offset, count, sessionId)
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    (for {
      i <- groupInvitationsDAO.validateExist(invitationId, sessionId)
      r <- accountGroupsDAO.exist(i.groupId, i.accountId)
      _ <- groupInvitationsDAO.update(i.groupId, i.accountId, GroupInvitationStatusType.accepted)
    } yield ((i, r))).flatMap(_ match {
      case (i, false) =>
        (for {
          _ <- accountGroupsDAO.create(i.accountId, i.groupId)
          _ <- groupInvitationsDAO.update(i.groupId, i.accountId, GroupInvitationStatusType.accepted)
          _ <- messagesDAO.create(i.groupId, MessageType.groupInvitation, i.accountId.toSessionId)
        } yield (()))
      case _ =>
        Future.value(())
    })
  }

  def reject(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    groupInvitationsDAO.exist(invitationId).flatMap(_ match {
      case true =>
        groupInvitationsDAO.update(invitationId, GroupInvitationStatusType.rejected, sessionId)
      case false =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
    })
  }

  def findPushNotifications(id: GroupInvitationId) : Future[List[PushNotification]] = {
    groupInvitationsDAO.find(id).flatMap(_ match {
      case Some(i) if i.notified == false => {
        val pushType = PushNotificationType.groupInvitation
        val postedAt = i.invitedAt
        val sessionId = i.by.toSessionId
        val url = deepLinkService.getInvitation(id)
        groupInvitationsDAO.findPushNotifications(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def updatePushNotifications(id: GroupInvitationId): Future[Unit] = {
    groupInvitationsDAO.updatePushNotifications(id)
  }

}
