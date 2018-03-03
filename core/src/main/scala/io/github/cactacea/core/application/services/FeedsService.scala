package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.PublishService
import io.github.cactacea.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.core.domain.models.Feed
import io.github.cactacea.core.domain.repositories._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, MediumId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FeedsService @Inject()(db: DatabaseService) {

  @Inject var feedsRepository: FeedsRepository = _
  @Inject var feedFavoritesRepository: FeedFavoritesRepository = _
  @Inject var reportsRepository: ReportsRepository = _
  @Inject var publishService: PublishService = _

  def create(message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, sessionId: SessionId): Future[FeedId] = {
    for {
      id <- db.transaction(feedsRepository.create(message, mediumIds, tags, privacyType, contentWarning, sessionId))
      _ <- publishService.enqueueFeed(id)
    } yield (id)
  }

  def delete(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      feedsRepository.delete(feedId, sessionId)
    }
  }

  def edit(feedId: FeedId, message: String, mediumIds: Option[List[MediumId]], tags: Option[List[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      feedsRepository.update(
        feedId,
        message,
        mediumIds,
        tags,
        privacyType,
        contentWarning,
        sessionId
      )
    }
  }

  def find(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedsRepository.findAll(accountId, since, offset, count, sessionId)
  }

  def find(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Feed]] = {
    feedsRepository.findAll(since, offset, count, sessionId)
  }

  def find(feedId: FeedId, sessionId: SessionId): Future[Feed] = {
    feedsRepository.find(
      feedId,
      sessionId
    )
  }

  def report(feedId: FeedId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      reportsRepository.createFeedReport(feedId, reportType, sessionId)
    }
  }

}


//  @Inject var deliveryFeedsRepository: DeliveryFeedsRepository = _

//  @Inject var queueService: OldQueueService = _
//  @Inject var pushNotificationService: OldPushNotificationService = _
//    db.transaction {
//      for {
//        id <- feedsRepository.create(message, mediumIds, tags, privacyType, contentWarning, sessionId)
//        _ <- queueService.enqueueDeliverFeed(id)
//      } yield (id)
//    }
//  def deliver(feedId: FeedId): Future[Unit] = {
//    db.transaction {
//      for {
//        _ <- deliveryFeedsRepository.create(feedId)
//        a <- queueService.enqueueNoticeFeed(feedId)
//      } yield (a)
//    }
//  }
//
//      _ <- deliveryFeedsRepository.create(id)
//  def notice(feedId: FeedId): Future[Unit] = {
//    deliveryFeedsRepository.findAll(feedId).flatMap(_ match {
//      case Some(feeds) =>
//        Future.traverseSequentially(feeds) { feed =>
//          db.transaction {
//            for {
//              accountIds <- pushNotificationService.notifyFeed(feed.accountId, feed.displayName, feed.tokens, feed.feedId, feed.postedAt)
//              _ <- deliveryFeedsRepository.updateNotified(feed.feedId, accountIds)
//            } yield (accountIds.size == feed.tokens.size)
//          }
//        }.flatMap(_.filter(_ == false).size match {
//          case 0L =>
//            db.transaction {
//              deliveryFeedsRepository.updateNotified(feedId).flatMap(_ => Future.Unit)
//            }
//          case _ =>
//            Future.exception(PushNotificationException())
//        })
//      case None =>
//        Future.Unit
//    })
//  }

