package io.github.cactacea.core.application.services

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.PermissionType

@Singleton
class ScopesService {

  def validate(requirePermission: PermissionType, permissionType: PermissionType): Future[Unit] = {
    Future.Unit
    // TODO
//    (requirePermission, permissionType) match {
//      case (PermissionType.comments, PermissionType.basic) =>
//        Future.exception(CactaceaException(OperationNotAllowed))
//      case _ =>
//        Future.Unit
//    }
  }
}
