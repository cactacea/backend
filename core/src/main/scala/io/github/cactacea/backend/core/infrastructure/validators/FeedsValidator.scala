package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.dao.{CommentsDAO, FeedsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AuthorityNotFound, CommentNotFound, FeedNotFound}

@Singleton
class FeedsValidator @Inject()(feedsDAO: FeedsDAO, commentsDAO: CommentsDAO) {

  def mustFind(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsDAO.find(feedId, sessionId).flatMap(_ match {
      case Some(f) =>
        Future.value(f)
      case None =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

  def mustExist(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedsDAO.exists(feedId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

  def mustFindOwner(feedId: FeedId, commentId: Option[CommentId]): Future[UserId] = {
    commentId match {
      case Some(id) =>
        commentsDAO.findOwner(id).flatMap(_ match {
          case Some(a) => Future.value(a)
          case None => Future.exception(CactaceaException(CommentNotFound))
        })
      case None =>
        feedsDAO.findOwner(feedId).flatMap(_ match {
          case Some(a) => Future.value(a)
          case None => Future.exception(CactaceaException(FeedNotFound))
        })
    }
  }

  def mustOwn(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedsDAO.own(feedId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AuthorityNotFound))
    })
  }


}

