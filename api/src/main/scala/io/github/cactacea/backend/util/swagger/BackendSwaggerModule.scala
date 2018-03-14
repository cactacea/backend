package io.github.cactacea.backend.swagger

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.google.inject.Provides
import com.jakehschwartz.finatra.swagger.{Resolvers, SwaggerModule, SwaggerTypeRegister}
import io.github.cactacea.core.infrastructure.identifiers._
import io.swagger.models.auth.{ApiKeyAuthDefinition, In, OAuth2Definition}
import io.swagger.models.{Info, Swagger}
import io.swagger.util.{Json, PrimitiveType}

import scala.collection.JavaConverters._

object SampleSwagger extends Swagger {
  Json.mapper().setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy)

  Resolvers.register()

  val map: Map[Class[_], PrimitiveType] = Map(
    classOf[AccountId] -> PrimitiveType.LONG,
    classOf[AccountGroupId] -> PrimitiveType.LONG,
    classOf[AccountId] -> PrimitiveType.LONG,
    classOf[AccountReportId] -> PrimitiveType.LONG,
    classOf[BlockId] -> PrimitiveType.LONG,
    classOf[CommentId] -> PrimitiveType.LONG,
    classOf[CommentLikeId] -> PrimitiveType.LONG,
    classOf[CommentReportId] -> PrimitiveType.LONG,
    classOf[DeviceId] -> PrimitiveType.LONG,
    classOf[FeedId] -> PrimitiveType.LONG,
    classOf[FeedLikeId] -> PrimitiveType.LONG,
    classOf[FeedReportId] -> PrimitiveType.LONG,
    classOf[FriendRequestId] -> PrimitiveType.LONG,
    classOf[GroupId] -> PrimitiveType.LONG,
    classOf[GroupInvitationId] -> PrimitiveType.LONG,
    classOf[GroupReportId] -> PrimitiveType.LONG,
    classOf[MediumId] -> PrimitiveType.LONG,
    classOf[MessageId] -> PrimitiveType.LONG,
    classOf[NotificationId] -> PrimitiveType.LONG,
    classOf[SessionId] -> PrimitiveType.LONG,
    classOf[StampId] -> PrimitiveType.LONG,
    classOf[TimelineFeedId] -> PrimitiveType.LONG
  )
  SwaggerTypeRegister.setExternalTypes(map.asJava)

}

object BackendSwaggerModule extends SwaggerModule {

  val swaggerUI = SampleSwagger

  @Provides
  def swagger: Swagger = {

    val info = new Info()
      .description("Cactacea / Cactacea backend API for web and mobile applications")
      .version("1.0.0")
      .title("Cactacea backend API")

    val scopes = Map(
      "basic" -> "to read a user's profile info and media (granted by default)",
      "comments" -> "to post and delete comments on a user's behalf",
      "groups" -> "to create and delete groups",
      "follower_list" -> "to read the list of followers and followed-by users",
      "likes" -> "to read any public profile info and media on a user’s behalf",
      "public_content" -> "to read a user's profile info and media (granted by default)",
      "relationships" -> "to follow and unfollow accounts on a user's behalf"
    )

    val oauth = new OAuth2Definition()
    oauth.setAuthorizationUrl("/auth")
    oauth.setScopes(scopes.asJava)

    val apiKey = new ApiKeyAuthDefinition()
    apiKey.setIn(In.HEADER)
    apiKey.setName("X-API-KEY")

    swaggerUI.info(info)
    swaggerUI.addSecurityDefinition("api_key", apiKey)
    swaggerUI.addSecurityDefinition("cactacea_oauth", oauth)

    swaggerUI
  }
}