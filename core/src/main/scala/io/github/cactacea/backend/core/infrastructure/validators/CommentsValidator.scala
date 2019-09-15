package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.dao.CommentsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AuthorityNotFound, CommentNotFound}

@Singleton
class CommentsValidator @Inject()(commentsDAO: CommentsDAO) {

  def mustFind(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsDAO.find(commentId, sessionId).flatMap(_ match {
      case Some(c) =>
        Future.value(c)
      case None =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def mustExist(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentsDAO.exists(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def mustExist(tweetId: TweetId, commentId: Option[CommentId], sessionId: SessionId): Future[Unit] = {
    commentId match {
      case Some(id) =>
        commentsDAO.exists(tweetId, id, sessionId).flatMap(_ match {
          case true =>
            Future.Unit
          case false =>
            Future.exception(CactaceaException(CommentNotFound))
        })
      case None =>
        Future.Done
    }
  }

  def mustHasAuthority(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentsDAO.own(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AuthorityNotFound))
    })
  }



}

