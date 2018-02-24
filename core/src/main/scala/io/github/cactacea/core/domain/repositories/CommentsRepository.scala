package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.factories.CommentFactory
import io.github.cactacea.core.domain.models.Comment
import io.github.cactacea.core.infrastructure.dao.{CommentsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

@Singleton
class CommentsRepository {

  @Inject var commentsDAO: CommentsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def findAll(feedId: FeedId, since: Option[Long], count: Option[Int], sessionId: SessionId): Future[List[Comment]] = {
    for {
      _ <- validationDAO.existFeeds(feedId, sessionId)
      r <- commentsDAO.findAll(feedId, since, count, sessionId).map(_.map(t => CommentFactory.create(t._1, t._2, t._3)))
    } yield (r)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsDAO.find(commentId, sessionId).flatMap(_ match {
      case Some((c, a, r)) =>
        Future.value(CommentFactory.create(c, a, r))
      case None =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      _ <- validationDAO.existFeeds(feedId, sessionId)
      r <- commentsDAO.create(feedId, message, sessionId)
    } yield (r)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existComments(commentId, sessionId)
      _ <- commentsDAO.delete(commentId, sessionId)
    } yield (Future.value(Unit))
  }

}
