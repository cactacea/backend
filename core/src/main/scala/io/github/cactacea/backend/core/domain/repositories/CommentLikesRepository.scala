package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}

@Singleton
class CommentLikesRepository @Inject()(
                                        commentLikesDAO: CommentLikesDAO,
                                        validationRepository: ValidationDAO
                                      ) {

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationRepository.existComment(commentId, sessionId)
      _ <- validationRepository.notExistCommentLike(commentId, sessionId)
      _ <- commentLikesDAO.create(commentId, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationRepository.existComment(commentId, sessionId)
      _ <- validationRepository.existCommentLike(commentId, sessionId)
      _ <- commentLikesDAO.delete(commentId, sessionId)
    } yield (Future.value(Unit))
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- validationRepository.existComment(commentId, sessionId)
      r <- commentLikesDAO.findAll(commentId, since, offset, count, sessionId)
        .map(_.map(t => Account(t._1, t._2, t._3)))
    } yield (r)
  }


}