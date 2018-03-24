package io.github.cactacea.backend.util.swagger

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.jakehschwartz.finatra.swagger.SwaggerTypeRegister
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.swagger.models.Swagger
import io.swagger.util.{Json, PrimitiveType}

import scala.collection.JavaConverters._

object BackendSwagger extends Swagger {
  Json.mapper().setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy)

  val map: Map[Class[_], PrimitiveType] = Map(
    classOf[AccountId]          -> PrimitiveType.LONG,
    classOf[AccountGroupId]     -> PrimitiveType.LONG,
    classOf[AccountId]          -> PrimitiveType.LONG,
    classOf[AccountReportId]    -> PrimitiveType.LONG,
    classOf[BlockId]            -> PrimitiveType.LONG,
    classOf[CommentId]          -> PrimitiveType.LONG,
    classOf[CommentLikeId]      -> PrimitiveType.LONG,
    classOf[CommentReportId]    -> PrimitiveType.LONG,
    classOf[DeviceId]           -> PrimitiveType.LONG,
    classOf[FeedId]             -> PrimitiveType.LONG,
    classOf[FeedLikeId]         -> PrimitiveType.LONG,
    classOf[FeedReportId]       -> PrimitiveType.LONG,
    classOf[FriendRequestId]    -> PrimitiveType.LONG,
    classOf[GroupId]            -> PrimitiveType.LONG,
    classOf[GroupInvitationId]  -> PrimitiveType.LONG,
    classOf[GroupReportId]      -> PrimitiveType.LONG,
    classOf[MediumId]           -> PrimitiveType.LONG,
    classOf[MessageId]          -> PrimitiveType.LONG,
    classOf[NotificationId]     -> PrimitiveType.LONG,
    classOf[SessionId]          -> PrimitiveType.LONG,
    classOf[StampId]            -> PrimitiveType.LONG,
  )
  SwaggerTypeRegister.setExternalTypes(map.asJava)

}