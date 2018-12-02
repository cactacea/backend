package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MediumId, MessageId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors

@Singleton
class MessagesRepository @Inject()(
                                    messagesDAO:  MessagesDAO,
                                    accountMessagesDAO: AccountMessagesDAO,
                                    accountGroupsDAO: AccountGroupsDAO,
                                    validationDAO: ValidationDAO
                                  ) {

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    (message, mediumId) match {
      case (Some(m), _) =>
        create(groupId, m, sessionId)
      case (_, Some(m)) =>
        create(groupId, m, sessionId)
      case _ =>
        Future.exception(CactaceaException(CactaceaErrors.requiredFieldMissingValidationError("Message or Medium Id required.")))
    }
  }

  private def create(groupId: GroupId, message: String, sessionId: SessionId): Future[MessageId] = {
    for {
      _  <- validationDAO.existGroupAccount(sessionId.toAccountId, groupId)
      id  <- messagesDAO.create(groupId, Some(message), None, sessionId)
      _  <- accountMessagesDAO.create(groupId, id, sessionId)
      _  <- accountGroupsDAO.updateUnreadCount(groupId)
    } yield (id)
  }

  private def create(groupId: GroupId, mediumId: MediumId, sessionId: SessionId): Future[MessageId] = {
    for {
      _  <- validationDAO.existGroupAccount(sessionId.toAccountId, groupId)
      _  <- validationDAO.existMediums(mediumId, sessionId)
      id <- messagesDAO.create(groupId, None, Some(mediumId), sessionId)
      _  <- accountMessagesDAO.create(groupId, id, sessionId)
      _  <- accountGroupsDAO.updateUnreadCount(groupId)
    } yield (id)
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
      } yield (Future.value(Unit))
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountMessagesDAO.delete(sessionId.toAccountId, groupId).flatMap(_ => Future.Unit)
  }

  def findAll(groupId: GroupId,
              since: Option[Long],
              offset: Int,
              count: Int,
              ascending: Boolean,
              sessionId: SessionId): Future[List[Message]] = {
    for {
      _ <- validationDAO.existGroup(groupId, sessionId)
      _ <- validationDAO.existGroupAccount(sessionId.toAccountId, groupId)
      r <- accountMessagesDAO.findAll(groupId, since, offset, count, ascending, sessionId)
    } yield (r)

  }

}
