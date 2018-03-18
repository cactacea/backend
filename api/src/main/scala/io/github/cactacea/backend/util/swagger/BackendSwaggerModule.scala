package io.github.cactacea.backend.swagger

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.google.inject.Provides
import com.jakehschwartz.finatra.swagger.{SwaggerModule, SwaggerTypeRegister}
import io.github.cactacea.core.infrastructure.identifiers._
import io.swagger.models.auth.{ApiKeyAuthDefinition, In, OAuth2Definition}
import io.swagger.models.{Info, Swagger, Tag}
import io.swagger.util.{Json, PrimitiveType}

import scala.collection.JavaConverters._

object SampleSwagger extends Swagger {
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

object BackendSwaggerModule extends SwaggerModule {

  val swaggerUI = SampleSwagger

  @Provides
  def swagger: Swagger = {

    val info = new Info()
      .description("Cactacea / Cactacea backend API for web and mobile applications")
      .version("0.1.0-SNAPSHOT")
      .title("Cactacea backend API")

    val scopes = Map(
      "basic"         -> "to read a user's profile info and media (granted by default)",
      "comments"      -> "to post and delete comments on a user's behalf",
      "groups"        -> "to create and delete groups",
      "follower_list" -> "to read the list of followers and followed-by users",
      "likes"         -> "to read any public profile info and media on a user’s behalf",
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

    swaggerUI.addTag(new Tag().name("Accounts").description("Manage accounts"))
    swaggerUI.addTag(new Tag().name("Blocks").description("Manage blocks"))
    swaggerUI.addTag(new Tag().name("Comments").description("Manage comments"))
    swaggerUI.addTag(new Tag().name("Feeds").description("Manage feeds"))
    swaggerUI.addTag(new Tag().name("Friends").description("Manage friends"))
    swaggerUI.addTag(new Tag().name("Followers").description("Manage followers"))
    swaggerUI.addTag(new Tag().name("Groups").description("Manage groups"))
    swaggerUI.addTag(new Tag().name("Invitations").description("Manage group invitations"))
    swaggerUI.addTag(new Tag().name("Mediums").description("Manage media"))
    swaggerUI.addTag(new Tag().name("Messages").description("Manage messages"))
    swaggerUI.addTag(new Tag().name("Mutes").description("Manage mutes"))
    swaggerUI.addTag(new Tag().name("Reports").description("Manage reports"))
    swaggerUI.addTag(new Tag().name("Friend Requests").description("Manage friend requests"))
    swaggerUI.addTag(new Tag().name("Session").description("Manage session"))
    swaggerUI.addTag(new Tag().name("Sessions").description("Manage sessions"))
    swaggerUI.addTag(new Tag().name("Social Accounts").description("Manage social accounts"))
    swaggerUI.addTag(new Tag().name("Settings").description("Manage session settings"))
    swaggerUI.addTag(new Tag().name("OAuth2").description("Provide Oauth2 features"))
    swaggerUI.addTag(new Tag().name("Resource").description("Manage resources"))
    swaggerUI.addTag(new Tag().name("System").description("Health checking and etc"))
    swaggerUI
  }

}