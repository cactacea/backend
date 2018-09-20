package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.session.{PutSessionAccountName, PutSessionPassword, PutSessionProfile, PutSessionProfileImage}
import io.github.cactacea.backend.swagger.CactaceaDocController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.{Account, SocialAccount}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNameAlreadyUsed,  MediumNotFound}
import io.swagger.models.Swagger


@Singleton
class SessionController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaDocController {

  protected implicit val swagger = s


  protected val tagName = "Session"

  @Inject private var accountsService: AccountsService = _
  @Inject private var sessionService: SessionsService = _
  @Inject private var settingsService: SettingsService = _

  prefix(apiPrefix) {

    getWithPermission("/session")(Permissions.basic) { o =>
      o.summary("Get basic information about session account")
        .tag(tagName)
        .responseWith[Account](Status.Ok.code, successfulMessage)

    } { _: Request =>
      accountsService.find(
        SessionContext.id
      )
    }


    deleteWithPermission("/session")(Permissions.basic) { o =>
      o.summary("Sign out")
        .tag(tagName)
        .responseWith(Status.NoContent.code, successfulMessage)

    } { _: Request =>
      sessionService.signOut(
        SessionContext.udid,
        SessionContext.id
      ).map(_ => response.noContent)
    }


    putWithPermission("/session/account_name")(Permissions.basic) { o =>
      o.summary("Update the account name")
        .tag(tagName)
        .request[PutSessionAccountName]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[AccountNameAlreadyUsed.type]](AccountNameAlreadyUsed.status.code, AccountNameAlreadyUsed.message)


    } { request: PutSessionAccountName =>
      accountsService.update(
        request.name,
        SessionContext.id
      ).map(_ => response.noContent)
    }


    putWithPermission("/session/password")(Permissions.basic) { o =>
      o.summary("Update the password")
        .tag(tagName)
        .request[PutSessionPassword]
        .responseWith(Status.NoContent.code, successfulMessage)

    } { request: PutSessionPassword =>
      accountsService.update(
        request.oldPassword,
        request.newPassword,
        SessionContext.id
      ).map(_ => response.noContent)
    }


    putWithPermission("/session/profile")(Permissions.basic) { o =>
      o.summary("Update the profile")
        .tag(tagName)
        .request[PutSessionProfile]
        .responseWith(Status.NoContent.code, successfulMessage)

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

    putWithPermission("/session/profile_image")(Permissions.basic) { o =>
      o.summary("Update the profile image")
        .tag(tagName)
        .request[PutSessionProfileImage]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[MediumNotFound.type]](MediumNotFound.status.code, MediumNotFound.message)

    }  { request: PutSessionProfileImage =>
      accountsService.updateProfileImage(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/session/profile_image")(Permissions.basic) { o =>
      o.summary("Remove the profile image")
        .tag(tagName)
        .responseWith(Status.NoContent.code, successfulMessage)

    }  { _: Request =>
      accountsService.deleteProfileImage(
        SessionContext.id
      ).map(_ => response.noContent)
    }


    getWithPermission("/social_accounts")(Permissions.basic) { o =>
      o.summary("Get status abount social accounts")
        .tag("Social Accounts")
        .responseWith[Array[SocialAccount]](Status.Ok.code, successfulMessage)

    } { _: Request =>
      settingsService.findSocialAccounts(
        SessionContext.id
      )
    }

  }

}
