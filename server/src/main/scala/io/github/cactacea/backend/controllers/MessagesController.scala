package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.message.{DeleteMessages, GetMessages, PostMessage}
import io.github.cactacea.backend.models.responses.MessageCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class MessagesController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Messages"

  @Inject private var messagesService: MessagesService = _

  prefix(apiPrefix) {

    getWithPermission("/messages")(Permissions.basic) { o =>
      o.summary("Search messages")
        .tag(tagName)
        .request[GetMessages]
        .responseWith[Message](Status.Ok.code, successfulMessage)

        .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

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

    deleteWithPermission("/messages")(Permissions.messages) { o =>
      o.summary("Delete messages form a group")
        .tag(tagName)
        .request[DeleteMessages]
        .responseWith(Status.NoContent.code, successfulMessage)

    } { request: DeleteMessages =>
      messagesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/messages")(Permissions.messages) { o =>
      o.summary("Post a message to a group")
        .tag(tagName)
        .request[PostMessage]
        .responseWith[MessageCreated](Status.Created.code, successfulMessage)
        .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)
        .responseWith[Array[AccountNotJoinedType]](AccountNotJoined.status.code, AccountNotJoined.message)
        .responseWith[Array[MediumNotFoundType]](MediumNotFound.status.code, MediumNotFound.message)

    } { request: PostMessage =>
      messagesService.create(
        request.id,
        request.message,
        request.mediumId,
        SessionContext.id
      ).map(MessageCreated(_)).map(response.created(_))
    }

  }

}
