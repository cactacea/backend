package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.DeliveryMessage
import io.github.cactacea.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, DeliveryMessagesDAO, MessagesDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MessageId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError._

@Singleton
class DeliveryMessagesRepository {

  @Inject var messagesDAO: MessagesDAO = _
  @Inject var accountMessagesDAO: AccountMessagesDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _
  @Inject var deliveryMessagesDAO: DeliveryMessagesDAO = _

  def create(messageId: MessageId): Future[Boolean] = {
    messagesDAO.findUndelivered(messageId).flatMap(_ match {
      case Some(m) =>
        accountGroupsDAO.findGroupId(messageId, m.by.toSessionId).flatMap(_ match {
          case Some(groupId) =>
            for {
              a <- messagesDAO.updateDelivered(messageId)
              b <- accountMessagesDAO.create(groupId, messageId, m.by.toSessionId)
              c <- accountGroupsDAO.updateUnreadCount(groupId)
            } yield (a && b && c)
          case None =>
            Future.exception(CactaceaException(GroupNotFound))
        })
      case None =>
        Future.False
    })
  }

  def findAll(messageId: MessageId): Future[Option[List[DeliveryMessage]]] = {
    messagesDAO.findUnNotified(messageId).flatMap(_ match {
      case Some(m) =>
        deliveryMessagesDAO.findTokens(messageId).map({y =>
          val r = y.groupBy(c => (c._2, c._4)).map({
            case (((displayName, showMessage), t)) =>
              val tokens = t.map(r => (r._3, r._1))
              DeliveryMessage(
                accountId         = m.by,
                displayName       = displayName,
                messageId         = m.id,
                message           = m.message,
                mediumId          = m.mediumId,
                postedAt          = m.postedAt,
                tokens            = tokens,
                show              = showMessage
              )
          }).toList
          Some(r)
        })
      case None =>
        Future.None
    })
  }

  def updateNotified(messageId: MessageId): Future[Boolean] = {
    messagesDAO.updateNotified(messageId)
  }

  def updateNotified(messageId: MessageId, accountIds: List[AccountId]): Future[Boolean] = {
    accountMessagesDAO.updateNotified(messageId, accountIds)
  }

}
