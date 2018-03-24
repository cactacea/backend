package io.github.cactacea.backend.swagger

import com.google.inject.{Inject, Provides}
import com.jakehschwartz.finatra.swagger.SwaggerModule
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.backend.util.swagger.BackendSwagger
import io.swagger.models.auth.{ApiKeyAuthDefinition, In, OAuth2Definition}
import io.swagger.models.{Info, Swagger, Tag}

import scala.collection.JavaConverters._

object BackendSwaggerModule extends SwaggerModule {

  val swaggerUI = BackendSwagger

  @Provides
  def swagger: Swagger = {

    val info = new Info()
      .description("Cactacea / Cactacea backend API for web and mobile applications")
      .version("0.1.0-SNAPSHOT")
      .title("Cactacea backend API")

    val scopes = Permissions.values.map(t => (t.value -> t.description)).toMap
    val oauth = new OAuth2Definition()
    oauth.setScopes(scopes.asJava)

    val apiKey = new ApiKeyAuthDefinition()
    apiKey.setIn(In.HEADER)
    apiKey.setName("X-API-KEY")

    swaggerUI.info(info)
    swaggerUI.addSecurityDefinition("api_key", apiKey)
    swaggerUI.addSecurityDefinition("oauth", oauth)

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