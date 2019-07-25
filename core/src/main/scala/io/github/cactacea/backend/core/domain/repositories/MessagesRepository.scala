package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountMessagesValidator, GroupsValidator, MediumsValidator}


class MessagesRepository @Inject()(
                                    accountGroupsValidator: AccountGroupsValidator,
                                    accountMessagesValidator: AccountMessagesValidator,
                                    groupsValidator: GroupsValidator,
                                    mediumsValidator: MediumsValidator,
                                    accountMessagesDAO: AccountMessagesDAO,
                                    accountGroupsDAO: AccountGroupsDAO,
                                    messagesDAO:  MessagesDAO
                                  ) {

  def createText(groupId: GroupId, message: String, sessionId: SessionId): Future[Message] = {
    for {
      _  <- accountGroupsValidator.exist(sessionId.toAccountId, groupId)
      id  <- messagesDAO.create(groupId, Some(message), None, sessionId)
      _  <- accountMessagesDAO.create(groupId, id, sessionId)
      _  <- accountGroupsDAO.updateUnreadCount(groupId)
      m <- accountMessagesValidator.find(id, sessionId)
    } yield (m)
  }

  def createMedium(groupId: GroupId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    for {
      _  <- accountGroupsValidator.exist(sessionId.toAccountId, groupId)
      _  <- mediumsValidator.exist(mediumId, sessionId)
      id <- messagesDAO.create(groupId, None, Some(mediumId), sessionId)
      _  <- accountMessagesDAO.create(groupId, id, sessionId)
      _  <- accountGroupsDAO.updateUnreadCount(groupId)
      m <- accountMessagesValidator.find(id, sessionId)
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
      } yield (())
    }
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountMessagesDAO.delete(sessionId.toAccountId, groupId)
  }

  def find(groupId: GroupId,
           since: Option[Long],
           offset: Int,
           count: Int,
           ascending: Boolean,
           sessionId: SessionId): Future[List[Message]] = {
    for {
      _ <- groupsValidator.exist(groupId, sessionId)
      _ <- accountGroupsValidator.exist(sessionId.toAccountId, groupId)
      r <- accountMessagesDAO.find(groupId, since, offset, count, ascending, sessionId)
    } yield (r)

  }



}
