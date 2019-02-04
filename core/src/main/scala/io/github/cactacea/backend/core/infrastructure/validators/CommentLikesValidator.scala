package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.CommentLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{CommentAlreadyLiked, CommentNotLiked}

@Singleton
class CommentLikesValidator @Inject()(commentLikesDAO: CommentLikesDAO) {

  def notExist(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentLikesDAO.exist(commentId, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(CommentAlreadyLiked))
    })
  }

  def exist(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentLikesDAO.exist(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(CommentNotLiked))
    })
  }


}
