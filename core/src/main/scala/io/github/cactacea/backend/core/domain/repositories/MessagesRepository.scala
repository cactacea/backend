package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountMessagesValidator, GroupsValidator, MediumsValidator}


class MessagesRepository @Inject()(
                                    accountGroupsDAO: AccountGroupsDAO,
                                    accountGroupsValidator: AccountGroupsValidator,
                                    accountMessagesDAO: AccountMessagesDAO,
                                    accountMessagesValidator: AccountMessagesValidator,
                                    groupsDAO: GroupsDAO,
                                    groupsValidator: GroupsValidator,
                                    mediumsValidator: MediumsValidator,
                                    messagesDAO:  MessagesDAO
                                  ) {

  def createText(groupId: GroupId, message: String, sessionId: SessionId): Future[Message] = {
    for {
      _ <- accountGroupsValidator.mustJoined(sessionId.toAccountId, groupId)
      c <- groupsDAO.findAccountCount(groupId)
      i <- messagesDAO.create(groupId, message, c, sessionId)
      _ <- accountMessagesDAO.create(groupId, i, sessionId)
      _ <- accountGroupsDAO.updateUnreadCount(groupId)
      m <- accountMessagesValidator.mustFind(i, sessionId)
    } yield (m)
  }

  def createMedium(groupId: GroupId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    for {
      _ <- accountGroupsValidator.mustJoined(sessionId.toAccountId, groupId)
      _ <- mediumsValidator.mustOwn(mediumId, sessionId)
      c <- groupsDAO.findAccountCount(groupId)
      i <- messagesDAO.create(groupId, mediumId, c, sessionId)
      _ <- accountMessagesDAO.create(groupId, i, sessionId)
      _ <- accountGroupsDAO.updateUnreadCount(groupId)
      m <- accountMessagesValidator.mustFind(i, sessionId)
    } yield (m)
  }

  private def updateReadStatus(messages: List[Message], sessionId: SessionId): Future[Unit] = {
    val m = messages.filter(_.unread)
    if (m.size == 0) {
      Future.Unit
    } else {
      val ids = m.map(_.id)
      for {
        _ <- messagesDAO.updateReadCount(ids)
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
      _ <- accountGroupsValidator.mustJoined(sessionId.toAccountId, groupId)
      r <- accountMessagesDAO.find(groupId, since, offset, count, ascending, sessionId)
      _ <- updateReadStatus(r, sessionId)
    } yield (r)

  }

}
