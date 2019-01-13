package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.domain.enums.PushNotificationType
import io.github.cactacea.backend.core.domain.models.{Message, PushNotification}
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class MessagesRepository @Inject()(
                                    accountMessagesDAO: AccountMessagesDAO,
                                    accountGroupsDAO: AccountGroupsDAO,
                                    groupsDAO: GroupsDAO,
                                    mediumsDAO: MediumsDAO,
                                    messagesDAO:  MessagesDAO,
                                    deepLinkService: DeepLinkService,
                                  ) {

  def createText(groupId: GroupId, message: String, sessionId: SessionId): Future[Message] = {
    for {
      _  <- accountGroupsDAO.validateExist(sessionId.toAccountId, groupId)
      id  <- messagesDAO.create(groupId, Some(message), None, sessionId)
      _  <- accountMessagesDAO.create(groupId, id, sessionId)
      _  <- accountGroupsDAO.updateUnreadCount(groupId)
      m <- accountMessagesDAO.validateFind(id, sessionId)
    } yield (m)
  }

  def createMedium(groupId: GroupId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    for {
      _  <- accountGroupsDAO.validateExist(sessionId.toAccountId, groupId)
      _  <- mediumsDAO.validateExist(mediumId, sessionId)
      id <- messagesDAO.create(groupId, None, Some(mediumId), sessionId)
      _  <- accountMessagesDAO.create(groupId, id, sessionId)
      _  <- accountGroupsDAO.updateUnreadCount(groupId)
      m <- accountMessagesDAO.validateFind(id, sessionId)
    } yield (m)
  }

  def updateReadStatus(messages: List[Message], sessionId: SessionId): Future[Unit] = {
    val m = messages.filter(_.unread)
    if (m.size == 0) {
      Future.Unit
    } else {
      val ids = m.map(_.id)
      for {
        _ <- messagesDAO.updateReadAccountCount(ids)
        _ <- accountMessagesDAO.updateUnread(ids, sessionId)
      } yield (Unit)
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountMessagesDAO.delete(sessionId.toAccountId, groupId).flatMap(_ => Future.Unit)
  }

  def find(groupId: GroupId,
           since: Option[Long],
           offset: Int,
           count: Int,
           ascending: Boolean,
           sessionId: SessionId): Future[List[Message]] = {
    for {
      _ <- groupsDAO.validateExist(groupId, sessionId)
      _ <- accountGroupsDAO.validateExist(sessionId.toAccountId, groupId)
      r <- accountMessagesDAO.find(groupId, since, offset, count, ascending, sessionId)
    } yield (r)

  }


  def findPushNotifications(id: MessageId) : Future[List[PushNotification]] = {
    messagesDAO.findPushNotification(id).flatMap(_ match {
      case Some(m) if m.notified == false => {
        val postedAt = m.postedAt
        val sessionId = m.by.toSessionId
        val url = deepLinkService.getMessages(m.groupId, m.id)
        messagesDAO.findPushNotifications(id).map({ t =>
          t.groupBy({ g => (g.displayName, g.showContent)}).map({
            case ((displayName, showContent), fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val pushType = showContent match {
                case true =>
                  PushNotificationType.message
                case false =>
                  PushNotificationType.noDisplayedMessage
              }
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def updatePushNotifications(id: MessageId, accountIds: List[AccountId]): Future[Unit] = {
    accountMessagesDAO.updateNotified(id, accountIds)
  }

}
