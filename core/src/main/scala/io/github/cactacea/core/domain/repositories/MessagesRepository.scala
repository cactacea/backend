package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, MediumId, MessageId, SessionId}

@Singleton
class MessagesRepository {

  @Inject var groupsDAO: GroupsDAO = _
  @Inject var groupAccountsDAO: GroupAccountsDAO = _
  @Inject var messagesDAO:  MessagesDAO = _
  @Inject var accountMessagesDAO: AccountMessagesDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    val ids = Some(mediumId.toList)
    for {
      _             <- validationDAO.existGroups(groupId)
      _             <- validationDAO.existGroupAccounts(sessionId.toAccountId, groupId)
      _             <- validationDAO.existMediums(ids, sessionId)
      accountCount  <- groupAccountsDAO.findCount(groupId)
      messageId     <- messagesDAO.create(groupId, message, accountCount, mediumId, sessionId)
      _             <- groupsDAO.update(groupId, Some(messageId), sessionId)
      _             <- accountMessagesDAO.create(groupId, messageId, sessionId)
      _             <- accountGroupsDAO.updateUnreadCount(groupId)
    } yield (messageId)
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
      _ <- validationDAO.existGroups(groupId)
      _ <- validationDAO.existGroupAccounts(sessionId.toAccountId, groupId)
    } yield (Unit)).flatMap(_ =>
      if (ascending) {
        accountMessagesDAO.findOlder(groupId, since, offset, count, sessionId)
          .map(l => l.map(t => Message(t._1, t._2, t._3, t._4, t._5)))
      } else {
        accountMessagesDAO.findEarlier(groupId, since, offset, count, sessionId)
          .map(l => l.map(t => Message(t._1, t._2, t._3, t._4, t._5)))
      }
    )
  }

}
