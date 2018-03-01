package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.domain.models.Feed
import io.github.cactacea.core.infrastructure.dao.{FeedsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, MediumId, SessionId}
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

@Singleton
class FeedsRepository {

  @Inject var feedsDAO: FeedsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def create(message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, sessionId: SessionId): Future[FeedId] = {
    val ids = mediumIds.map(_.distinct)
    for {
      _ <- validationDAO.existMediums(ids, sessionId)
      r <- feedsDAO.create(message, ids, tags, privacyType, contentWarning, sessionId)
    } yield (r)
  }

  def update(feedId: FeedId, message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, sessionId: SessionId): Future[Unit] = {
    val ids = mediumIds.map(_.distinct)
    (for {
      _ <- validationDAO.existMediums(ids, sessionId)
      r <- feedsDAO.update(feedId, message, ids, tags, privacyType, contentWarning, sessionId)
    } yield (r)).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedsDAO.delete(feedId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    for {
      _ <- validationDAO.existAccounts(accountId, sessionId)
      r <- feedsDAO.findAll(accountId, since, offset, count, sessionId).map(_.map(t => Feed(t._1, t._2, t._3)))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedsDAO.findAll(since, offset, count, sessionId)
      .map(_.map(t => Feed(t._1, t._2, t._3)))
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsDAO.find(feedId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(Feed(t._1, t._2, t._3, t._4, t._5))
      case None =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

}