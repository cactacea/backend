package io.github.cactacea.backend.swagger

import com.jakehschwartz.finatra.swagger.SwaggerController

trait BackendController extends SwaggerController {

  protected val successfulMessage = "successful operation"
  protected val validationErrorMessage = "validation error occurred"

}
