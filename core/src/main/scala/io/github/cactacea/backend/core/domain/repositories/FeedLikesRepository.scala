package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.{Feed, User}
import io.github.cactacea.backend.core.infrastructure.dao.FeedLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{FeedLikesValidator, FeedsValidator, UsersValidator}

@Singleton
class FeedLikesRepository @Inject()(
                                     usersValidator: UsersValidator,
                                     feedsValidator: FeedsValidator,
                                     feedLikesValidator: FeedLikesValidator,
                                     feedLikesDAO: FeedLikesDAO
                                   ) {

  def create(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      _ <- feedLikesValidator.mustNotLiked(feedId, sessionId)
      _ <- feedLikesDAO.create(feedId, sessionId)
    } yield (())
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      _ <- feedLikesValidator.mustLiked(feedId, sessionId)
      _ <- feedLikesDAO.delete(feedId, sessionId)
    } yield (())
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- feedLikesDAO.find(userId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedLikesDAO.find(sessionId.userId, since, offset, count, sessionId)
  }

  def findUsers(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[User]] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      r <- feedLikesDAO.findUsers(feedId, since, offset, count, sessionId)
    } yield (r)
  }

}
