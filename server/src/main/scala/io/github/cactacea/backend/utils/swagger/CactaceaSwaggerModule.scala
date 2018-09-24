package io.github.cactacea.backend.swagger

import com.google.inject.Provides
import io.cactacea.finagger.SwaggerModule
import io.github.cactacea.backend.utils.swagger.CactaceaSwagger
import io.swagger.models.auth.ApiKeyAuthDefinition
import io.swagger.models.{Info, Swagger, Tag}

import scala.collection.JavaConverters._

object CactaceaSwaggerModule extends SwaggerModule {

  @Provides
  def swagger: Swagger = {

    val info = new Info()
      .description("Cactacea / Cactacea backend API for web and mobile applications")
      .version("0.5.1-SNAPSHOT")
      .title("Cactacea backend API")

    info.setVendorExtension("x-logo", Map("url" -> "https://avatars3.githubusercontent.com/u/36766951?s=100&v=4\"", "altText" -> "Cactacea logo").asJava)

    val swaggerDefine =
      CactaceaSwagger.info(info)

//    swaggerSecurityDefinitions.foreach {
//      case (name, definition) => swaggerDefine.securityDefinition(name, definition)
//    }

    swaggerDefine.addTag(new Tag().name("Accounts").description("Manage accounts"))
    swaggerDefine.addTag(new Tag().name("Blocks").description("Manage blocks"))
    swaggerDefine.addTag(new Tag().name("Comments").description("Manage comments"))
    swaggerDefine.addTag(new Tag().name("Feeds").description("Manage feeds"))
    swaggerDefine.addTag(new Tag().name("Friends").description("Manage friends"))
    swaggerDefine.addTag(new Tag().name("Follows").description("Manage follows"))
    swaggerDefine.addTag(new Tag().name("Groups").description("Manage groups"))
    swaggerDefine.addTag(new Tag().name("Invitations").description("Manage group invitations"))
    swaggerDefine.addTag(new Tag().name("Mediums").description("Manage media"))
    swaggerDefine.addTag(new Tag().name("Messages").description("Manage messages"))
    swaggerDefine.addTag(new Tag().name("Mutes").description("Manage mutes"))
    swaggerDefine.addTag(new Tag().name("Reports").description("Manage reports"))
    swaggerDefine.addTag(new Tag().name("Friend Requests").description("Manage friend requests"))
    swaggerDefine.addTag(new Tag().name("Session").description("Manage session"))
    swaggerDefine.addTag(new Tag().name("Sessions").description("Manage sessions"))
    swaggerDefine.addTag(new Tag().name("Social Accounts").description("Manage social accounts"))
    swaggerDefine.addTag(new Tag().name("Settings").description("Manage session settings"))
    swaggerDefine.addTag(new Tag().name("System").description("Health checking and etc"))
    swaggerDefine

  }


  /**
    * Security definitions to apply to swagger
    *
    * @return
    */
  protected def swaggerSecurityDefinitions: Seq[(String, ApiKeyAuthDefinition)] = {
//    val apiKey = new ApiKeyAuthDefinition()
//    apiKey.setIn(In.HEADER)
//    apiKey.setName("X-API-KEY")
//    Seq(
//      ("api_key", apiKey)
//    )
    Nil
  }

//  def configureDocumentation(router: HttpRouter): Unit = {
//    // create swagger
//    swaggerInfo
//
//    router.add(new SwaggerController(swagger = SwaggerDefinition))
//    router.add[WebjarsController]
//
//    // add the converters in order to swagger
//    swaggerConverters.reverse.foreach(ModelConverters.getInstance().addConverter)
//  }


  //  protected lazy val swaggerInfo = {
  //    val info =
  //      SwaggerDefinition.info(
  //        new Info()
  //          .description(documentation.description)
  //          .version(documentation.version)
  //          .title(documentation.title)
  //      )
  //
  //    swaggerSecurityDefinitions.foreach {
  //      case (name, definition) => info.securityDefinition(name, definition)
  //    }
  //
  //    info
  //  }

  //    val scopes = Permissions.values.map(t => (t.value -> t.description)).toMap
  //    val accessCode = new OAuth2Definition().accessCode("/oauth2/authorization", "/oauth2/token")
  //    accessCode.setScopes(scopes.asJava)

  //    val apiKey = new ApiKeyAuthDefinition()
  //    apiKey.setIn(In.HEADER)
  //    apiKey.setName("X-API-KEY")
  //
  //    swaggerUI.info(info)
  //
  //    swaggerUI.addSecurityDefinition("api_key", apiKey)

  //    swaggerUI.addSecurityDefinition("accessCode", accessCode)

}