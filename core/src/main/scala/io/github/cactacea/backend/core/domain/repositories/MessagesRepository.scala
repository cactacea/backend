package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MediumId, SessionId}

@Singleton
class MessagesRepository @Inject()(
                                    accountMessagesDAO: AccountMessagesDAO,
                                    accountGroupsDAO: AccountGroupsDAO,
                                    groupsDAO: GroupsDAO,
                                    mediumsDAO: MediumsDAO,
                                    messagesDAO:  MessagesDAO
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

}
