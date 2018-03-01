package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.interfaces.{PushNotificationService, QueueService}
import io.github.cactacea.core.domain.models.GroupInvite
import io.github.cactacea.core.domain.repositories.{DeliveryGroupInvitesRepository, GroupInvitesRepository}
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.DatabaseService
import io.github.cactacea.core.util.exceptions.PushNotificationException

@Singleton
class GroupInvitesService @Inject()(db: DatabaseService) {

  @Inject var groupInvitesRepository: GroupInvitesRepository = _
  @Inject var queueService: QueueService = _

  @Inject var deliveryGroupInvitesRepository: DeliveryGroupInvitesRepository = _
  @Inject var pushNotificationService: PushNotificationService = _

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInviteId]] = {
    for {
      ids <- db.transaction(groupInvitesRepository.create(accountIds, groupId, sessionId))
      _ <- Future.traverseSequentially(ids) {id => queueService.enqueueNoticeGroupInvite(id)}
    } yield (ids)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInviteId] = {
    for {
      id <- db.transaction(groupInvitesRepository.create(accountId, groupId, sessionId))
      _ <- queueService.enqueueNoticeGroupInvite(id)
    } yield (id)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[GroupInvite]] = {
    groupInvitesRepository.findAll(since, offset, count, sessionId)
  }

  def accept(inviteId: GroupInviteId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupInvitesRepository.accept(inviteId, sessionId)
    }
  }

  def reject(inviteId: GroupInviteId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      groupInvitesRepository.reject(inviteId, sessionId)
    }
  }

  def notice(groupInviteId: GroupInviteId): Future[Unit] = {
    deliveryGroupInvitesRepository.findAll(groupInviteId).flatMap(_ match {
      case Some(invites) =>
        Future.traverseSequentially(invites) { invite =>
          db.transaction {
            for {
              accountIds <- pushNotificationService.notifyGroupInvite(invite.accountId, invite.displayName, invite.tokens, invite.groupId, invite.invitedAt)
            } yield (accountIds.size == invite.tokens.size)
          }
        }.flatMap(_.filter(_ == false).size match {
          case 0L =>
            db.transaction {
              deliveryGroupInvitesRepository.updateNotified(groupInviteId).flatMap(_ => Future.Unit)
            }
          case _ =>
            Future.exception(PushNotificationException())
        })
      case None =>
        Future.Unit
    })
  }

}
