package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ TweetType, ReportType, TweetPrivacyType}
import io.github.cactacea.backend.core.domain.models.Tweet
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId, TweetId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{TweetsValidator, MediumsValidator, UsersValidator}

@Singleton
class TweetsRepository @Inject()(
                                  usersValidator: UsersValidator,
                                  userTweetsDAO: UserTweetsDAO,
                                  tweetsDAO: TweetsDAO,
                                  tweetTagsDAO: TweetTagsDAO,
                                  tweetMediumsDAO: TweetMediumsDAO,
                                  tweetReportsDAO: TweetReportsDAO,
                                  tweetsValidator: TweetsValidator,
                                  mediumsValidator: MediumsValidator,
                                  notificationsDAO: NotificationsDAO
                               ) {

  def create(message: String,
             mediumIds: Option[Seq[MediumId]],
             tags: Option[Seq[String]],
             privacyType: TweetPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[TweetId] = {

    val ids = mediumIds.map(_.distinct)
    for {
      _ <- mediumsValidator.mustExist(ids, sessionId)
      i <- tweetsDAO.create(message, ids, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- tweetTagsDAO.create(i, tags)
      _ <- tweetMediumsDAO.create(i, mediumIds)
      _ <- userTweetsDAO.create(i, sessionId)
      _ <- notificationsDAO.create(i, sessionId)
    } yield (i)
  }

  def update(tweetId: TweetId,
             message: String,
             mediumIds: Option[Seq[MediumId]],
             tags: Option[Seq[String]],
             privacyType: TweetPrivacyType,
             contentWarning: Boolean,
             expiration: Option[Long],
             sessionId: SessionId): Future[Unit] = {

    val ids = mediumIds.map(_.distinct)
    for {
      _ <- tweetsValidator.mustOwn(tweetId, sessionId)
      _ <- mediumsValidator.mustExist(ids, sessionId)
      _ <- tweetTagsDAO.delete(tweetId)
      _ <- tweetMediumsDAO.delete(tweetId)
      _ <- tweetsDAO.update(tweetId, message, ids, tags, privacyType, contentWarning, expiration, sessionId)
      _ <- tweetTagsDAO.create(tweetId, tags)
      _ <- tweetMediumsDAO.create(tweetId, mediumIds)
    } yield (())
  }

  def delete(tweetId: TweetId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- tweetsValidator.mustOwn(tweetId, sessionId)
      _ <- tweetsDAO.delete(tweetId, sessionId)
    } yield (())
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Tweet]] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- tweetsDAO.find(userId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, privacyType: Option[TweetPrivacyType], tweetType: TweetType, sessionId: SessionId): Future[Seq[Tweet]] = {
    tweetType match {
      case TweetType.posted =>
        tweetsDAO.find(sessionId.userId, since, offset, count, sessionId)
      case TweetType.received =>
        userTweetsDAO.find(since, offset, count, privacyType, sessionId)
    }
  }

  def find(tweetId: TweetId, sessionId: SessionId): Future[Tweet] = {
    tweetsValidator.mustFind(tweetId, sessionId)
  }

  def report(tweetId: TweetId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- tweetsValidator.mustExist(tweetId, sessionId)
      _ <- tweetReportsDAO.create(tweetId, reportType, reportContent, sessionId)
    } yield (())
  }

}
