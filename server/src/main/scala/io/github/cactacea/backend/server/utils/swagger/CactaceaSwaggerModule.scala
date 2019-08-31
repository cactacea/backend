package io.github.cactacea.backend.server.utils.swagger

import com.google.inject.Provides
import io.cactacea.finagger
import io.cactacea.finagger.SwaggerModule
import io.github.cactacea.backend.CactaceaBuildInfo
import io.github.cactacea.backend.core.util.configs.Config
import io.swagger.models._
import io.swagger.models.auth.{ApiKeyAuthDefinition, In}

object CactaceaSwaggerModule extends SwaggerModule {

  @Provides
  def swagger: Swagger = {

    val info = new Info()
      .title("Cactacea backend API")
      .description("Cactacea / Cactacea backend API for web and mobile applications")
      .version(CactaceaBuildInfo.version)

    val swaggerDefine = finagger.CactaceaSwagger.info(info)

    // Model defines
    swaggerDefine.addDefinition("CactaceaError", errorResponseScheme("CactaceaError"))

    // Tags
    swaggerDefine.addTag(new Tag().name("Accounts").description("Manage accounts"))
    swaggerDefine.addTag(new Tag().name("Blocks").description("Manage blocks"))
    swaggerDefine.addTag(new Tag().name("Comments").description("Manage comments"))
    swaggerDefine.addTag(new Tag().name("Feeds").description("Manage feeds"))
    swaggerDefine.addTag(new Tag().name("Groups").description("Manage groups"))
    swaggerDefine.addTag(new Tag().name("Invitations").description("Manage invitations"))
    swaggerDefine.addTag(new Tag().name("Mediums").description("Manage media"))
    swaggerDefine.addTag(new Tag().name("Messages").description("Manage messages"))
    swaggerDefine.addTag(new Tag().name("Session").description("Manage session"))
    swaggerDefine.addTag(new Tag().name("Sessions").description("Manage sessions"))
    swaggerDefine.addTag(new Tag().name("Settings").description("Manage session settings"))
    swaggerDefine.addTag(new Tag().name("System").description("Health checking and etc"))


    val apiKeyAuthDefinition = new ApiKeyAuthDefinition(Config.auth.headerNames.apiKey, In.HEADER)
    swaggerDefine.securityDefinition("api_key", apiKeyAuthDefinition)

    val authorizationKeyAuthDefinition = new ApiKeyAuthDefinition(Config.auth.headerNames.authorizationKey, In.HEADER)
    swaggerDefine.securityDefinition("authorization", authorizationKeyAuthDefinition)

//    val scopes = Permissions.values.map(t => (t.value -> t.description)).toMap
//    val accessCode = new OAuth2Definition().accessCode("http://localhost:9000/oauth2/authorization", "http://localhost:9000/oauth2/token")
//    accessCode.setScopes(scopes.asJava)
//    swaggerDefine.addSecurityDefinition("cactacea_auth", accessCode)

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