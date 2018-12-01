package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.dao.{CommentsDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class CommentsRepository @Inject()(
                                    commentsDAO: CommentsDAO,
                                    validationDAO: ValidationDAO
                                  ) {

  def findAll(feedId: FeedId, since: Option[Long], count: Int, sessionId: SessionId): Future[List[Comment]] = {
    for {
      _ <- validationDAO.existFeed(feedId, sessionId)
      r <- commentsDAO.findAll(feedId, since, count, sessionId).map(_.map({ case (c, a, r) => Comment(c, a, r)}))
    } yield (r)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsDAO.find(commentId, sessionId).flatMap(_ match {
      case Some((c, a, r)) =>
        Future.value(Comment(c, a, r))
      case None =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      _ <- validationDAO.existFeed(feedId, sessionId)
      r <- commentsDAO.create(feedId, message, sessionId)
    } yield (r)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existComment(commentId, sessionId)
      _ <- commentsDAO.delete(commentId, sessionId)
    } yield (Future.value(Unit))
  }

}
