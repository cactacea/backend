package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.DeliveryGroupInvite
import io.github.cactacea.core.infrastructure.dao.{DeliveryGroupInvitesDAO, GroupInvitesDAO}
import io.github.cactacea.core.infrastructure.identifiers.GroupInviteId

@Singleton
class DeliveryGroupInvitesRepository {

  @Inject var groupInvitesDAO: GroupInvitesDAO = _
  @Inject var deliveryGroupInvitesDAO: DeliveryGroupInvitesDAO = _

  def findAll(groupInviteId: GroupInviteId): Future[Option[List[DeliveryGroupInvite]]] = {
    groupInvitesDAO.findUnNotified(groupInviteId).flatMap(_ match {
      case Some(g) => {
        deliveryGroupInvitesDAO.findTokens(groupInviteId).map({y =>
          val r = y.groupBy(_._2).map({
            case (((displayName), t)) =>
              val tokens = t.map(r => (r._3, r._1))
              DeliveryGroupInvite(
                groupId           = g.groupId,
                accountId         = g.by,
                displayName       = displayName,
                invitedAt         = g.invitedAt,
                tokens            = tokens
              )
          }).toList
          Some(r)
        })
      }
      case None =>
        Future.None
    })
  }

  def updateNotified(groupInviteId: GroupInviteId): Future[Boolean] = {
    groupInvitesDAO.updateNotified(groupInviteId, true)
  }

}
