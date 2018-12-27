package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, MediumId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class FeedsRepository @Inject()(
                                 accountsDAO: AccountsDAO,
                                 accountFeedsDAO: AccountFeedsDAO,
                                 feedsDAO: FeedsDAO,
                                 mediumsDAO: MediumsDAO
                               ) {

  def create(message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[FeedId] = {

    val ids = mediumIds.map(_.distinct)
    for {
      _ <- mediumsDAO.validateExist(ids, sessionId)
      id <- feedsDAO.create(message, ids, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- accountFeedsDAO.create(id, sessionId)
    } yield (id)
  }

  def update(feedId: FeedId,
             message: String,
             mediumIds: Option[List[MediumId]],
             tags: Option[List[String]],
             privacyType: FeedPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[Unit] = {

    val ids = mediumIds.map(_.distinct)
    for {
      _ <- mediumsDAO.validateExist(ids, sessionId)
      _ <- feedsDAO.validateExist(feedId, sessionId)
      _ <- feedsDAO.update(feedId, message, ids, tags, privacyType, contentWarning, expiration, sessionId)
    } yield (Unit)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- feedsDAO.validateExist(feedId, sessionId)
      _ <- feedsDAO.delete(feedId, sessionId)
    } yield (Unit)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      r <- feedsDAO.find(accountId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[FeedPrivacyType], sessionId: SessionId): Future[List[Feed]] = {
    for {
      r <- accountFeedsDAO.find(since, offset, count, privacyType, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Feed]] = {
    feedsDAO.find(since, offset, count, sessionId)
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsDAO.find(feedId, sessionId).flatMap(_ match {
      case Some(f) =>
        Future.value(f)
      case None =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

}
