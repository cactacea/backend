package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.FriendsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotFriend
import io.github.cactacea.backend.models.requests.account.DeleteFriend
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.auth.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class FriendsController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   friendsService: FriendsService,
                                   s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    deleteWithDoc("/accounts/:id/friends") { o =>
      o.summary("Remove friendship to a account")
        .tag(accountsTag)
        .operationId("unfriend")
        .request[DeleteFriend]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotFriend))))

    } { request: DeleteFriend =>
      friendsService.delete(
        request.id,
        CactaceaContext.id
      ).map(_ => response.ok)
    }

  }

}
