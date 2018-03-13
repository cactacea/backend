package io.github.cactacea.backend.swagger

import com.jakehschwartz.finatra.swagger.SwaggerController

trait BackendController extends SwaggerController {

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."

}
