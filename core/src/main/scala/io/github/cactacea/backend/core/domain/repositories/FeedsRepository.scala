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
                                 feedsDAO: FeedsDAO,
                                 accountFeedsDAO: AccountFeedsDAO,
                                 validationDAO: ValidationDAO
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
      _ <- validationDAO.existMediums(ids, sessionId)
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
      _ <- validationDAO.existMediums(ids, sessionId)
      _ <- validationDAO.existFeed(feedId, sessionId)
      r <- feedsDAO.update(feedId, message, ids, tags, privacyType, contentWarning, expiration, sessionId)
    } yield (r)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existFeed(feedId, sessionId)
      r <- feedsDAO.delete(feedId, sessionId)
    } yield (r)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      r <- feedsDAO.findAll(accountId, since, offset, count, sessionId)
        .map(_.map({ case (f, ft, m) => Feed(f, ft, m)}))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], privacyType: Option[FeedPrivacyType], sessionId: SessionId): Future[List[Feed]] = {
    for {
      r <- accountFeedsDAO.findAll(since, offset, count, privacyType, sessionId)
        .map(_.map(t => Feed(t._2, t._3, t._4, t._5, t._6)))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedsDAO.findAll(since, offset, count, sessionId)
      .map(_.map({ case (f, ft, m) => Feed(f, ft, m)}))
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsDAO.find(feedId, sessionId).flatMap(_ match {
      case Some((f, ft, m, a, r)) =>
        Future.value(Feed(f, ft, m, a, r))
      case None =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

}
