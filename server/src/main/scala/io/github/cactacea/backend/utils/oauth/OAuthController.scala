package io.github.cactacea.backend.utils.oauth

import com.twitter.finatra.http.{Controller, PermissionRouteDSL}

trait OAuthController extends Controller with PermissionRouteDSL {
  self: Controller =>

  implicit protected val convertToPermissionRouteDSL = PermissionRouteDSL.convert _
}
