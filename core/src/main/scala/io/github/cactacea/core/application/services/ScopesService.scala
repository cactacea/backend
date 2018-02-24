package io.github.cactacea.core.application.services

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.PermissionType
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.OperationNotAllowed

@Singleton
class ScopesService {

  def validate(requirePermission: PermissionType, permissionType: PermissionType): Future[Unit] = {
    (requirePermission, permissionType) match {
      case (PermissionType.readWrite, PermissionType.readOnly) =>
        Future.exception(CactaceaException(OperationNotAllowed))
      case _ =>
        Future.Unit
    }
  }
}
