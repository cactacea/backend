package io.github.cactacea.backend.swagger

import com.jakehschwartz.finatra.swagger.SwaggerController
import io.github.cactacea.backend.util.swagger.PermissionController

trait BackendController extends SwaggerController with PermissionController {

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."


}
