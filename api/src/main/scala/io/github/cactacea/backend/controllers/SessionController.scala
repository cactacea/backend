package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.{Account, SocialAccount}
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNameAlreadyUsed, MediumNotFound}
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger


@Singleton
class SessionController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Session"

  @Inject private var accountsService: AccountsService = _
  @Inject private var sessionService: SessionsService = _

  getWithDoc("/session") { o =>
    o.summary("Get basic information about session account")
      .tag(tagName)
      .responseWith[Account](Status.Ok.code, successfulMessage)

  } { _: Request =>
    accountsService.find(
      SessionContext.id
    )
  }


  deleteWithDoc("/session") { o =>
    o.summary("Sign out")
      .tag(tagName)
      .responseWith(Status.NoContent.code, successfulMessage)

  } { _: Request =>
    sessionService.signOut(
      SessionContext.udid,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  putWithDoc("/session/account_name") { o =>
    o.summary("Update the account name")
      .tag(tagName)
      .request[PutSessionAccountName]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, AccountNameAlreadyUsed.message)
    

  } { request: PutSessionAccountName =>
    accountsService.update(
      request.accountName,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  putWithDoc("/session/password") { o =>
    o.summary("Update the password")
      .tag(tagName)
      .request[PutSessionPassword]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)

  } { request: PutSessionPassword =>
    accountsService.update(
      request.oldPassword,
      request.newPassword,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  putWithDoc("/session/profile") { o =>
    o.summary("Update the profile")
      .tag(tagName)
      .request[PutSessionProfile]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)

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
    o.summary("Update the profile image")
      .tag(tagName)
      .request[PutSessionProfileImage]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[NotFound]](Status.NotFound.code, MediumNotFound.message)

  }  { request: PutSessionProfileImage =>
    accountsService.updateProfileImage(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/session/profile_image") { o =>
    o.summary("Remove the profile image")
      .tag(tagName)
      .responseWith(Status.NoContent.code, successfulMessage)

  }  { _: Request =>
    accountsService.deleteProfileImage(
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var settingsService: SettingsService = _

  getWithDoc("/social_accounts") { o =>
    o.summary("Get status abount social accounts")
      .tag("Social Accounts")
      .responseWith[Array[SocialAccount]](Status.Ok.code, successfulMessage)

  } { _: Request =>
    settingsService.findSocialAccounts(
      SessionContext.id
    )
  }

}
