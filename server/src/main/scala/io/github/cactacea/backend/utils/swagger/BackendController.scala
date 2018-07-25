package io.github.cactacea.backend.swagger

import io.github.cactacea.backend.utils.swagger.PermissionController
import io.github.cactacea.swagger.SwaggerController

trait BackendController extends SwaggerController with PermissionController {

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."


}
