package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.DeliveryFeed
import io.github.cactacea.core.infrastructure.dao.{AccountFeedsDAO, DeliveryFeedsDAO, FeedsDAO, TimeLineDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId}

@Singleton
class DeliveryFeedsRepository {

  @Inject var feedsDAO: FeedsDAO = _
  @Inject var accountFeedsDAO: AccountFeedsDAO = _
  @Inject var deliveryFeedsDAO: DeliveryFeedsDAO = _
  @Inject var timelineDAO: TimeLineDAO = _

  def create(feedId: FeedId): Future[Boolean] = {
    feedsDAO.findUndelivered(feedId).flatMap(_ match {
      case Some(f) =>
        for {
          a <- accountFeedsDAO.create(feedId, f.by.toSessionId)
          b <- timelineDAO.create(feedId, f.by.toSessionId)
          c <- feedsDAO.updateDelivered(feedId, true)
        } yield (a && b && c)
      case None =>
        Future.False
    })
  }

  def findAll(feedId: FeedId): Future[Option[List[DeliveryFeed]]] = {
    feedsDAO.findUnNotified(feedId).flatMap(_ match {
      case Some(f) =>
        deliveryFeedsDAO.findTokens(feedId).map({y =>
          val r = y.groupBy(_._2).map({
            case (((displayName), t)) =>
              val tokens = t.map(r => (r._3, r._1))
              DeliveryFeed(
                feedId            = f.id,
                accountId         = f.by,
                displayName       = displayName,
                message           = f.message,
                postedAt          = f.postedAt,
                tokens            = tokens
              )
          }).toList
          Some(r)
        })
      case None =>
        Future.None
    })
  }


  def updateNotified(feedId: FeedId): Future[Boolean] = {
    feedsDAO.updateNotified(feedId, true)
  }

  def updateNotified(feedId: FeedId, accountIds: List[AccountId]): Future[Boolean] = {
    accountFeedsDAO.updateNotified(feedId, accountIds, true)
  }

}
