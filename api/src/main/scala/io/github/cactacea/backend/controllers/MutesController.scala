package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account.{DeleteMute, PostMute}
import io.github.cactacea.backend.models.requests.session.GetSessionMutes
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.services.MutesService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors.{AccountAlreadyBlocked, AccountNotBlocked, _}
import io.swagger.models.Swagger

@Singleton
class MutesController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Mutes"

  @Inject private var mutesService: MutesService = _

  getWithDoc(c.rootPath + "/session/mutes") { o =>
    o.summary("Get accounts list session account muted")
      .tag(tagName)
      .request[GetSessionMutes]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)


  } { request: GetSessionMutes =>
    mutesService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc(c.rootPath + "/accounts/:id/mutes") { o =>
    o.summary("Mute this account")
      .tag(tagName)
      .request[PostMute]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[AccountAlreadyBlockedType]](AccountAlreadyBlocked.status.code, AccountAlreadyBlocked.message)
      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: PostMute =>
    mutesService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc(c.rootPath + "/accounts/:id/mutes") { o =>
    o.summary("UnMute this account")
      .tag(tagName)
      .request[DeleteMute]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[AccountNotBlockedType]](AccountNotBlocked.status.code, AccountNotBlocked.message)
      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: DeleteMute =>
    mutesService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
