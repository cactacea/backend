package io.github.cactacea.backend.utils.auth

import com.twitter.finatra.http.{Controller, PermissionRouteDSL}

trait PermissionController extends Controller with PermissionRouteDSL {
  self: Controller =>

  implicit protected val convertToPermissionRouteDSL = PermissionRouteDSL.convert _
}
