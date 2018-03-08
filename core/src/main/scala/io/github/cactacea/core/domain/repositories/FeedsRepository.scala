package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.domain.models.Feed
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, MediumId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError._

@Singleton
class FeedsRepository {

  @Inject private var feedsDAO: FeedsDAO = _
  @Inject private var accountFeedsDAO: AccountFeedsDAO = _
  @Inject private var timelineDAO: TimeLineDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], sessionId: SessionId): Future[FeedId] = {
    val ids = mediumIds.map(_.distinct)
    for {
      _ <- validationDAO.existMedium(ids, sessionId)
      id <- feedsDAO.create(message, ids, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- accountFeedsDAO.create(id, sessionId)
      _ <- timelineDAO.create(id, sessionId)
    } yield (id)
  }

  def update(feedId: FeedId, message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, expiration: Option[Long], sessionId: SessionId): Future[Unit] = {
    val ids = mediumIds.map(_.distinct)
    (for {
      _ <- validationDAO.existMedium(ids, sessionId)
      r <- feedsDAO.update(feedId, message, ids, tags, privacyType, contentWarning, expiration, sessionId)
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
      _ <- validationDAO.existAccount(accountId, sessionId)
      r <- feedsDAO.findAll(accountId, since, offset, count, sessionId).map(_.map({ case (f, ft, m) => Feed(f, ft, m)}))
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