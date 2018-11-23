package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.message.{DeleteMessages, GetMessages, PostMessage}
import io.github.cactacea.backend.models.responses.MessageCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class MessagesController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, messagesService: MessagesService, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  protected val messagesTag = "Messages"

  prefix(apiPrefix) {

    getWithPermission("/messages")(Permissions.basic) { o =>
      o.summary("Search messages")
        .tag(messagesTag)
        .operationId("findMessages")
        .request[GetMessages]
        .responseWith[Message](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: GetMessages =>
      messagesService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        request.ascending,
        SessionContext.id
      )
    }

    postWithPermission("/messages")(Permissions.messages) { o =>
      o.summary("Post a message to a group")
        .tag(messagesTag)
        .operationId("postMessage")
        .request[PostMessage]
        .responseWith[MessageCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotJoined))))
    } { request: PostMessage =>
      messagesService.create(
        request.id,
        request.message,
        request.mediumId,
        SessionContext.id
      ).map(MessageCreated(_)).map(response.created(_))
    }

    deleteWithPermission("/messages")(Permissions.messages) { o =>
      o.summary("Delete messages form a group")
        .tag(messagesTag)
        .operationId("deleteMessage")
        .request[DeleteMessages]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: DeleteMessages =>
      messagesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}
