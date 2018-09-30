package com.twitter.finatra.http

import com.twitter.finagle.http.RouteIndex
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.{Permission, Permissions}
import io.swagger.models.{Operation, Swagger}

/**
  * To work around the accessibility of RouteDSL, this class is in "com.twitter.finatra.http" package
  */
object PermissionRouteDSL {
  implicit def convert(dsl: RouteDSL)(implicit swagger: Swagger): PermissionRouteDSL = new PermissionRouteDSLWrapper(dsl)(swagger)
}

trait PermissionRouteDSL extends SwaggerRouteDSL {

  def postWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                 name: String = "",
                                                                 admin: Boolean = false,
                                                                 routeIndex: Option[RouteIndex] = None)
                                                                 (scopes: Permission*)
                                                                 (doc: Operation => Unit)
                                                                 (callback: RequestType => ResponseType): Unit = {

    val securedDoc = createSecuredOperation(scopes)(doc)
    postWithDoc(route, name, admin, routeIndex)(securedDoc)(callback)
  }

  def getWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                                (scopes: Permission*)
                                                                (doc: Operation => Unit)
                                                                (callback: RequestType => ResponseType): Unit = {

    val securedDoc = createSecuredOperation(scopes)(doc)
    getWithDoc(route, name, admin, routeIndex)(securedDoc)(callback)
  }

  def putWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                                (scopes: Permission*)
                                                                (doc: Operation => Unit)
                                                                (callback: RequestType => ResponseType): Unit = {
    val securedDoc = createSecuredOperation(scopes)(doc)
    putWithDoc(route, name, admin, routeIndex)(securedDoc)(callback)
  }

  def patchWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                  name: String = "",
                                                                  admin: Boolean = false,
                                                                  routeIndex: Option[RouteIndex] = None)
                                                                  (scopes: Permission*)
                                                                  (doc: Operation => Unit)
                                                                  (callback: RequestType => ResponseType): Unit = {

    val securedDoc = createSecuredOperation(scopes)(doc)
    patchWithDoc(route, name, admin, routeIndex)(securedDoc)(callback)
  }

  def headWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                 name: String = "",
                                                                 admin: Boolean = false,
                                                                 routeIndex: Option[RouteIndex] = None)
                                                                 (scopes: Permission*)
                                                                 (doc: Operation => Unit)
                                                                 (callback: RequestType => ResponseType): Unit = {

    val securedDoc = createSecuredOperation(scopes)(doc)
    headWithDoc(route, name, admin, routeIndex)(securedDoc)(callback)
  }

  def deleteWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                   name: String = "",
                                                                   admin: Boolean = false,
                                                                   routeIndex: Option[RouteIndex] = None)
                                                                   (scopes: Permission*)
                                                                   (doc: Operation => Unit)
                                                                   (callback: RequestType => ResponseType): Unit = {
    val securedDoc = createSecuredOperation(scopes)(doc)
    deleteWithDoc(route, name, admin, routeIndex)(securedDoc)(callback)
  }

  def optionsWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                    name: String = "",
                                                                    admin: Boolean = false,
                                                                    routeIndex: Option[RouteIndex] = None)
                                                                    (scopes: Permission*)
                                                                    (doc: Operation => Unit)
                                                                    (callback: RequestType => ResponseType): Unit = {
    val securedDoc = createSecuredOperation(scopes)(doc)
    optionsWithDoc(route, name, admin, routeIndex)(securedDoc)(callback)
  }

  protected def createSecuredOperation(scopes: Seq[Permission])(doc: Operation => Unit) = {
    import scala.collection.JavaConverters._
    val SecuredOperation: Operation => Unit = { o =>
      doc(o)
      if (scopes.size > 0) {
        o.addSecurity("cactacea_auth", scopes.map(_.value).asJava)
      }
      o.addSecurity("api_key", List[String]().asJava)
      Unit
    }
    SecuredOperation
  }

//  protected def createCallback[RequestType: Manifest, ResponseType: Manifest](scopes: Seq[Permission])(callback: RequestType => ResponseType) = {
//    if (scopes.size > 0) {
//      val createCallback: RequestType => ResponseType = { request =>
//        if (validatePermission(scopes.toList) == true) {
//          callback(request)
//        } else {
//          throw CactaceaException(OperationNotAllowed)
//        }
//      }
//      createCallback
//    } else {
//      callback
//    }
//  }

  private def validatePermission(requireScopes: List[Permission]): Boolean = {
    if (SessionContext.permissions == Permissions.all) {
      true
    } else {
      val s = SessionContext.permissions
      val r = requireScopes.map(_.key).foldLeft(0) {_ + _}
      (s & r) > r
    }
  }

}

private class PermissionRouteDSLWrapper(override protected val dsl: RouteDSL)(implicit protected val swagger: Swagger) extends PermissionRouteDSL
