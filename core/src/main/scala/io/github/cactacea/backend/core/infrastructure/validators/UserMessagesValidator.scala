package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.dao.UserMessagesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{MessageId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.MessageNotFound

@Singleton
class UserMessagesValidator @Inject()(userMessagesDAO: UserMessagesDAO) {

  def mustFind(id: MessageId, sessionId: SessionId): Future[Message] = {
    userMessagesDAO.find(id, sessionId).flatMap(_ match {
      case Some(m) => Future.value(m)
      case None => Future.exception(CactaceaException(MessageNotFound))
    })
  }
}
