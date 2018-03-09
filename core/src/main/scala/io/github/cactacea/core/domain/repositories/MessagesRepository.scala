package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, MediumId, MessageId, SessionId}

@Singleton
class MessagesRepository {

  @Inject private var groupsDAO: GroupsDAO = _
  @Inject private var groupAccountsDAO: GroupAccountsDAO = _
  @Inject private var messagesDAO:  MessagesDAO = _
  @Inject private var accountMessagesDAO: AccountMessagesDAO = _
  @Inject private var accountGroupsDAO: AccountGroupsDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    val ids = Some(mediumId.toList)
    for {
      _  <- validationDAO.existGroup(groupId, sessionId)
      _  <- validationDAO.existGroupAccount(sessionId.toAccountId, groupId)
      _  <- validationDAO.existMediums(ids, sessionId)
      a  <- groupAccountsDAO.findCount(groupId)
      id  <- messagesDAO.create(groupId, message, a, mediumId, sessionId)
      _  <- groupsDAO.update(groupId, Some(id), sessionId)
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

  def findAll(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], ascending: Boolean, sessionId: SessionId): Future[List[Message]] = {
    (for {
      _ <- validationDAO.existGroup(groupId, sessionId)
      _ <- validationDAO.existGroupAccount(sessionId.toAccountId, groupId)
    } yield (Unit)).flatMap(_ =>
      if (ascending) {
        accountMessagesDAO.findOlder(groupId, since, offset, count, sessionId)
          .map(l => l.map({ case (m, am, i, a, r) => Message(m, am, i, a, r)}))
      } else {
        accountMessagesDAO.findEarlier(groupId, since, offset, count, sessionId)
          .map(l => l.map({ case (m, am, i, a, r) => Message(m, am, i, a, r)}))
      }
    )
  }

}
