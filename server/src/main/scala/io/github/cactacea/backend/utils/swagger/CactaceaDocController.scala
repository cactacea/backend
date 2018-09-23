package io.github.cactacea.backend.swagger

import io.cactacea.finagger.SwaggerController
import io.github.cactacea.backend.utils.auth.PermissionController

trait CactaceaDocController extends SwaggerController with PermissionController {

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."


}
