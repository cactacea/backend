package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.DeliveryComment
import io.github.cactacea.core.infrastructure.dao.{CommentsDAO, DeliveryCommentsDAO}
import io.github.cactacea.core.infrastructure.identifiers.CommentId

@Singleton
class DeliveryCommentsRepository {

  @Inject var commentsDAO: CommentsDAO = _
  @Inject var deliveryCommentsDAO: DeliveryCommentsDAO = _

  def findAll(commentId: CommentId): Future[Option[List[DeliveryComment]]] = {
    commentsDAO.findUnNotified(commentId).flatMap(_ match {
      case Some(c) =>
        deliveryCommentsDAO.findTokens(commentId).map({y =>
          val r = y.groupBy(_._2).map({
            case (((displayName), t)) =>
              val tokens = t.map(r => (r._3, r._1))
              DeliveryComment(
                commentId         = c.id,
                message           = c.message,
                accountId         = c.by,
                displayName       = displayName,
                postedAt          = c.postedAt,
                tokens            = tokens
              )
          }).toList
          Some(r)
        })
      case None =>
        Future.None
    })
  }

  def updateNotified(commentId: CommentId): Future[Boolean] = {
    commentsDAO.updateNotified(commentId)
  }

}
