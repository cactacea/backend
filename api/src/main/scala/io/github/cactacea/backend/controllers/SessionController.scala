package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class SessionController @Inject()(s: Swagger) extends Controller with SwaggerController {

  implicit protected val swagger = s

  private val tagName = "Session"

  @Inject private var accountsService: AccountsService = _
  @Inject private var sessionService: SessionsService = _

  getWithDoc("/session") { o =>
    o.summary("Get session account")
      .tag(tagName)
      .responseWith[Account](Status.Ok.code, "successful operation")

  } { _: Request =>
    accountsService.find(
      SessionContext.id
    )
  }


  deleteWithDoc("/session") { o =>
    o.summary("Sign out current logged in user session")
      .tag(tagName)
      .responseWith(Status.NoContent.code, "successful operation")

  } { _: Request =>
    sessionService.signOut(
      SessionContext.udid,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  putWithDoc("/session/account_name") { o =>
    o.summary("Update session account name")
      .tag(tagName)
      .bodyParam[PutSessionAccountName]("PutSessionAccountName", "new account name")
      .responseWith(Status.NoContent.code, "successful operation")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "account name already used")
    

  } { request: PutSessionAccountName =>
    accountsService.update(
      request.accountName,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  putWithDoc("/session/password") { o =>
    o.summary("Update session account password")
      .tag(tagName)
      .bodyParam[PutSessionPassword]("PutSessionPassword", "old password and new password")
      .responseWith(Status.NoContent.code, "successful operation")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred")

  } { request: PutSessionPassword =>
    accountsService.update(
      request.oldPassword,
      request.newPassword,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  putWithDoc("/session/profile") { o =>
    o.summary("Update session profile")
      .tag(tagName)
      .bodyParam[PutSessionProfile]("PutSessionProfile", "new profile information")
      .responseWith(Status.NoContent.code, "successful operation")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred")

  }  { request: PutSessionProfile =>
    accountsService.update(
      request.displayName,
      request.web,
      request.birthday,
      request.location,
      request.bio,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  putWithDoc("/session/profile_image") { o =>
    o.summary("Update session profile")
      .tag(tagName)
      .bodyParam[PutSessionProfileImage]("PutSessionProfileImage", "new profile image")
      .responseWith(Status.NoContent.code, "successful operation")
      .responseWith[Array[NotFound]](Status.NotFound.code, "image not found")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred")

  }  { request: PutSessionProfileImage =>
    accountsService.update(
      request.mediumId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
