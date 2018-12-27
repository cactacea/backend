package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.message.{DeleteMessages, GetMessages, PostMedium, PostText}
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
        .responseWith[Array[Message]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: GetMessages =>
      messagesService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.ascending,
        SessionContext.id
      )
    }

    postWithPermission("/messages/text")(Permissions.messages) { o =>
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
        SessionContext.id
      ).map(response.created(_))
    }

    postWithPermission("/messages/medium")(Permissions.messages) { o =>
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
        SessionContext.id
      ).map(response.created(_))
    }

    deleteWithPermission("/messages")(Permissions.messages) { o =>
      o.summary("Delete messages form a group")
        .tag(messagesTag)
        .operationId("delete")
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
