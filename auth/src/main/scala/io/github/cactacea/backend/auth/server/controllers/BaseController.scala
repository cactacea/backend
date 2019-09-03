package io.github.cactacea.backend.auth.server.controllers

import io.github.cactacea.finagger.SwaggerController

trait BaseController extends SwaggerController {

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."

  protected val sessionsTag = "Sessions"
  protected val sessionTag = "Session"
  protected val passwordTag = "Password"

}

