package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.responses.MessageCreated
import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.domain.repositories.{DeliveryMessagesRepository, MessagesRepository}
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.{PushNotificationService, QueueService}
import io.github.cactacea.core.util.exceptions.PushNotificationException

class MessagesService @Inject()(db: DatabaseService) {

  @Inject var messagesRepository: MessagesRepository = _
  @Inject var queueService: QueueService = _
  @Inject var deliveryMessagesRepository: DeliveryMessagesRepository = _
  @Inject var pushNotificationService: PushNotificationService = _

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageCreated] = {
    for {
      created <- db.transaction(messagesRepository.create(groupId, message, mediumId, sessionId).map(MessageCreated(_)))
      _ <- queueService.enqueueDeliverMessage(created.id)
    } yield (created)
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      messagesRepository.delete(groupId, sessionId).flatMap(_ => Future.Unit)
    }
  }

  def find(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], ascending: Boolean, sessionId: SessionId): Future[List[Message]] = {
    db.transaction {
      for {
        m <- messagesRepository.findAll(groupId, since, offset, count, ascending, sessionId)
        _ <- messagesRepository.updateReadStatus(m, sessionId)
      } yield (m)
    }
  }

  def deliver(messageId: MessageId): Future[Unit] = {
    db.transaction {
      for {
        _ <- deliveryMessagesRepository.create(messageId)
        a <- queueService.enqueueNoticeMessage(messageId)
      } yield (a)
    }
  }

  def notice(messageId: MessageId): Future[Unit] = {
    deliveryMessagesRepository.findAll(messageId).flatMap(_ match {
      case Some(messages) =>
        Future.traverseSequentially(messages) { message =>
          db.transaction {
            for {
              accountIds <- pushNotificationService.notifyMessage(message.accountId, message.displayName, message.tokens, message.messageId, message.message, message.mediumId, message.postedAt, message.show)
              _ <- deliveryMessagesRepository.updateNotified(message.messageId, accountIds)
            } yield (accountIds.size == message.tokens.size)
          }
        }.flatMap(_.filter(_ == false).size match {
          case 0L =>
            db.transaction {
              deliveryMessagesRepository.updateNotified(messageId).flatMap(_ => Future.Unit)
            }
          case _ =>
            Future.exception(PushNotificationException())
        })
      case None =>
        Future.Unit
    })
  }

}
