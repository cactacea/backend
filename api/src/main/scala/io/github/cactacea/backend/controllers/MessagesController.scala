package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.message._
import io.github.cactacea.backend.models.responses.MessageCreated
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class MessagesController extends Controller {

  @Inject private var messagesService: MessagesService = _

  get("/messages") { request: GetMessages =>
    messagesService.find(
      request.groupId,
      request.since,
      request.offset,
      request.count,
      request.ascending,
      SessionContext.id
    )
  }

  delete("/messages") { request: DeleteMessages =>
    messagesService.delete(
      request.groupId,
      SessionContext.id
    )
  }

  post("/messages") { request: PostMessage =>
    messagesService.create(
      request.groupId,
      request.groupMessage,
      request.mediumId,
      SessionContext.id
    ).map(MessageCreated(_)).map(response.created(_))
  }

}
