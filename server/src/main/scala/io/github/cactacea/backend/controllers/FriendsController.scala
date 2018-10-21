package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.FriendsService
import io.github.cactacea.backend.core.util.responses.BadRequest
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotFriend
import io.github.cactacea.backend.models.requests.account.DeleteFriend
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class FriendsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var friendsService: FriendsService = _

  prefix(apiPrefix) {

    deleteWithPermission("/accounts/:id/friends")(Permissions.relationships) { o =>
      o.summary("Remove friendship to a account")
        .tag(friendsTag)
        .operationId("deleteFriend")
        .request[DeleteFriend]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(AccountNotFriend))

    } { request: DeleteFriend =>
      friendsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}
