package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.FriendsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotFriend
import io.github.cactacea.backend.server.models.requests.account.DeleteFriend
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class FriendsController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   friendsService: FriendsService,
                                   s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(relationships).deleteWithDoc("/accounts/:id/friends") { o =>
      o.summary("Remove friendship to an account")
        .tag(accountsTag)
        .operationId("unfriend")
        .request[DeleteFriend]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotFriend))))

    } { request: DeleteFriend =>
      friendsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}
