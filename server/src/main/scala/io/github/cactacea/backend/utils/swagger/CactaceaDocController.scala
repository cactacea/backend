package io.github.cactacea.backend.swagger

import io.github.cactacea.backend.utils.swagger.PermissionController
import io.github.cactacea.swagger.SwaggerController

trait CactaceaDocController extends SwaggerController with PermissionController {

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."


}
