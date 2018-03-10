package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.message._
import io.github.cactacea.backend.models.responses.MessageCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Message
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotJoined, GroupNotFound, MediumNotFound}
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class MessagesController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s
  protected val tagName = "Messages"

  @Inject private var messagesService: MessagesService = _

  getWithDoc("/messages") { o =>
    o.summary("Get messages")
      .tag(tagName)
      .request[GetMessages]
      .responseWith[Message](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

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

  deleteWithDoc("/messages") { o =>
    o.summary("Delete a group messages")
      .tag(tagName)
      .request[DeleteMessages]
      .responseWith(Status.NoContent.code, successfulMessage)

  } { request: DeleteMessages =>
    messagesService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/messages") { o =>
    o.summary("Post a message")
      .tag(tagName)
      .request[PostMessage]
      .responseWith[MessageCreated](Status.Created.code, successfulMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)
      .responseWith[NotFound](Status.NotFound.code, AccountNotJoined.message)
      .responseWith[NotFound](Status.NotFound.code, MediumNotFound.message)

  } { request: PostMessage =>
    messagesService.create(
      request.id,
      request.groupMessage,
      request.mediumId,
      SessionContext.id
    ).map(MessageCreated(_)).map(response.created(_))
  }

}
