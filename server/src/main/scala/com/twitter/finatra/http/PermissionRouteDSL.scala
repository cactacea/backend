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

//    registerOperation(route, "post")(createOperation(scopes)(doc))
//    post(route, name, admin, routeIndex)(createCallback(scopes.toSeq)(callback))
    super.postWithDoc(route, name, admin, routeIndex)(doc)(callback)
  }

  def getWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                                (scopes: Permission*)
                                                                (doc: Operation => Unit)
                                                                (callback: RequestType => ResponseType): Unit = {

//    registerOperation(route, "get")(createOperation(scopes)(doc))
//    get(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
    super.getWithDoc(route, name, admin, routeIndex)(doc)(callback)
  }

  def putWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                                (scopes: Permission*)
                                                                (doc: Operation => Unit)
                                                                (callback: RequestType => ResponseType): Unit = {
//    registerOperation(route, "put")(createOperation(scopes)(doc))
//    put(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
    super.putWithDoc(route, name, admin, routeIndex)(doc)(callback)
  }

  def patchWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                  name: String = "",
                                                                  admin: Boolean = false,
                                                                  routeIndex: Option[RouteIndex] = None)
                                                                  (scopes: Permission*)
                                                                  (doc: Operation => Unit)
                                                                  (callback: RequestType => ResponseType): Unit = {
//    registerOperation(route, "patch")(createOperation(scopes)(doc))
//    patch(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
    super.patchWithDoc(route, name, admin, routeIndex)(doc)(callback)
  }

  def headWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                 name: String = "",
                                                                 admin: Boolean = false,
                                                                 routeIndex: Option[RouteIndex] = None)
                                                                 (scopes: Permission*)
                                                                 (doc: Operation => Unit)
                                                                 (callback: RequestType => ResponseType): Unit = {
//    registerOperation(route, "head")(createOperation(scopes)(doc))
//    head(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
    super.headWithDoc(route, name, admin, routeIndex)(doc)(callback)
  }

  def deleteWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                   name: String = "",
                                                                   admin: Boolean = false,
                                                                   routeIndex: Option[RouteIndex] = None)
                                                                   (scopes: Permission*)
                                                                   (doc: Operation => Unit)
                                                                   (callback: RequestType => ResponseType): Unit = {
//    registerOperation(route, "delete")(createOperation(scopes)(doc))
//    delete(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
    super.deleteWithDoc(route, name, admin, routeIndex)(doc)(callback)
  }

  def optionsWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                    name: String = "",
                                                                    admin: Boolean = false,
                                                                    routeIndex: Option[RouteIndex] = None)
                                                                    (scopes: Permission*)
                                                                    (doc: Operation => Unit)
                                                                    (callback: RequestType => ResponseType): Unit = {
//    registerOperation(route, "options")(createOperation(scopes)(doc))
//    options(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
    super.optionsWithDoc(route, name, admin, routeIndex)(doc)(callback)
  }

//  protected def createOperation(scopes: Seq[Permission])(doc: Operation => Unit) = {
//    import scala.collection.JavaConverters._
//    val doc2: Operation => Unit = { o =>
//      if (scopes.size > 0) {
//        o.addSecurity("accessCode", scopes.map(_.value).asJava)
//        o.addSecurity("application", scopes.map(_.value).asJava)
//      }
//      o.addSecurity("api_key", List[String]().asJava)
//      Unit
//    }
//    doc2
//  }
//
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

//  private def registerOperation(path: String, method: String)(doc: Operation => Unit): Unit = {
//    FinatraSwagger
//      .convert(swagger)
//      .registerOperation(prefixRoute(path), method, doc(new Operation))
//  }
//
//
//  //exact copy from Finatra RouteDSL class (it is defined as private there)
//  private def prefixRoute(route: String): String = {
//    contextWrapper {
//      contextVar().prefix match {
//        case prefix if prefix.nonEmpty && prefix.startsWith("/") => s"$prefix$route"
//        case prefix if prefix.nonEmpty && !prefix.startsWith("/") => s"/$prefix$route"
//        case _ => route
//      }
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
