package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.message.{DeleteMessages, GetMessages, PostMedium, PostText}
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class MessagesController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    messagesService: MessagesService,
                                    s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  protected val messagesTag = "Messages"

  prefix(apiPrefix) {

    getWithDoc("/messages") { o =>
      o.summary("Search messages")
        .tag(messagesTag)
        .operationId("findMessages")
        .request[GetMessages]
        .responseWith[Array[Message]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: GetMessages =>
      messagesService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.ascending,
        CactaceaContext.sessionId
      )
    }

    postWithDoc("/messages/text") { o =>
      o.summary("Send a text to a group")
        .tag(messagesTag)
        .operationId("postText")
        .request[PostText]
        .responseWith[Message](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotJoined))))
    } { request: PostText =>

      messagesService.createText(
        request.groupId,
        request.message,
        CactaceaContext.sessionId
      ).map(response.created(_))
    }

    postWithDoc("/messages/medium") { o =>
      o.summary("Send a medium to a group")
        .tag(messagesTag)
        .operationId("postMedium")
        .request[PostMedium]
        .responseWith[Message](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotJoined))))
    } { request: PostMedium =>

      messagesService.createMedium(
        request.groupId,
        request.mediumId,
        CactaceaContext.sessionId
      ).map(response.created(_))
    }

    deleteWithDoc("/messages") { o =>
      o.summary("Delete messages form a group")
        .tag(messagesTag)
        .operationId("deleteMessage")
        .request[DeleteMessages]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: DeleteMessages =>
      messagesService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}
