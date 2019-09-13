package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.SocialAuthenticationService
import io.github.cactacea.backend.auth.server.models.requests.social.GetSocialAuthentication

@Singleton
class SocialAuthenticationsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    socialAuthenticationService: SocialAuthenticationService
                                  ) extends Controller {

  prefix(apiPrefix) {

    get("/social/:provider/authenticate") { request: GetSocialAuthentication =>
      implicit val r = request.request
      socialAuthenticationService.authenticate(request.provider)
    }

  }

}
