package io.github.cactacea.backend.swagger

import com.google.inject.Provides
import io.cactacea.finagger.SwaggerModule
import io.github.cactacea.backend.CactaceaBuildInfo
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.utils.oauth.{Permissions}
import io.github.cactacea.backend.utils.swagger.CactaceaSwagger
import io.swagger.models._
import io.swagger.models.auth.{ApiKeyAuthDefinition, OAuth2Definition}

import scala.collection.JavaConverters._
import io.swagger.models.auth.In

object CactaceaSwaggerModule extends SwaggerModule {

  @Provides
  def swagger: Swagger = {

    val info = new Info()
      .title("Cactacea backend API")
      .description("Cactacea / Cactacea backend API for web and mobile applications")
      .version(CactaceaBuildInfo.version)

//    info.setVendorExtension("x-logo", Map("url" -> "https://avatars3.githubusercontent.com/u/36766951?s=100&v=4\"", "altText" -> "Cactacea logo").asJava)

    val swaggerDefine = CactaceaSwagger.info(info)

    // Model defines
    swaggerDefine.addDefinition("CactaceaError", errorResponseScheme("CactaceaError"))

    // Tags
    swaggerDefine.addTag(new Tag().name("Accounts").description("Manage accounts"))
    swaggerDefine.addTag(new Tag().name("Blocks").description("Manage blocks"))
    swaggerDefine.addTag(new Tag().name("Comments").description("Manage comments"))
    swaggerDefine.addTag(new Tag().name("Feeds").description("Manage feeds"))
    swaggerDefine.addTag(new Tag().name("Friends").description("Manage friends"))
    swaggerDefine.addTag(new Tag().name("Following").description("Manage following"))
    swaggerDefine.addTag(new Tag().name("Groups").description("Manage groups"))
    swaggerDefine.addTag(new Tag().name("Invitations").description("Manage group invitations"))
    swaggerDefine.addTag(new Tag().name("Mediums").description("Manage media"))
    swaggerDefine.addTag(new Tag().name("Messages").description("Manage messages"))
    swaggerDefine.addTag(new Tag().name("Mutes").description("Manage mutes"))
    swaggerDefine.addTag(new Tag().name("Friend Requests").description("Manage friend requests"))
    swaggerDefine.addTag(new Tag().name("Session").description("Manage session"))
    swaggerDefine.addTag(new Tag().name("Sessions").description("Manage sessions"))
    swaggerDefine.addTag(new Tag().name("Settings").description("Manage session settings"))
    swaggerDefine.addTag(new Tag().name("System").description("Health checking and etc"))


    val apiKeyAuthDefinition = new ApiKeyAuthDefinition(Config.auth.headerNames.apiKey, In.HEADER)
    swaggerDefine.securityDefinition("api_key", apiKeyAuthDefinition)

    val authorizationKeyAuthDefinition = new ApiKeyAuthDefinition(Config.auth.headerNames.authorizationKey, In.HEADER)
    swaggerDefine.securityDefinition("authorization", authorizationKeyAuthDefinition)

    val scopes = Permissions.values.map(t => (t.value -> t.description)).toMap
    val accessCode = new OAuth2Definition().accessCode("http://localhost:9000/oauth2/authorization", "http://localhost:9000/oauth2/token")
    accessCode.setScopes(scopes.asJava)
    swaggerDefine.addSecurityDefinition("cactacea_auth", accessCode)

    swaggerDefine

  }

  def errorResponseScheme(name: String): ModelImpl = {
    import io.swagger.models.ModelImpl
    import io.swagger.models.properties.{LongProperty, StringProperty}

    val code = new LongProperty().description("code")
    val message = new StringProperty().description("message")
    code.setRequired(true)
    message.setRequired(true)
    new ModelImpl().name(name).`type`("object")
      .property("code", code)
      .property("message", message)
  }

}
