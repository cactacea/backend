package com.twitter.finatra.http

import com.twitter.finagle.http.RouteIndex
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.{Permission, Permissions}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.OperationNotAllowed
import io.swagger.models.{Operation, Swagger}

import scala.collection.JavaConverters._

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
                                                                 (doc: Operation => Operation)
                                                                 (callback: RequestType => ResponseType): Unit = {

    registerOperation(route, "post")(createOperation(scopes)(doc))
    post(route, name, admin, routeIndex)(createCallback(scopes.toSeq)(callback))
  }

  def getWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                                (scopes: Permission*)
                                                                (doc: Operation => Operation)
                                                                (callback: RequestType => ResponseType): Unit = {

    registerOperation(route, "get")(createOperation(scopes)(doc))
    get(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
  }

  def putWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                                (scopes: Permission*)
                                                                (doc: Operation => Operation)
                                                                (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "put")(createOperation(scopes)(doc))
    put(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
  }

  def patchWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                  name: String = "",
                                                                  admin: Boolean = false,
                                                                  routeIndex: Option[RouteIndex] = None)
                                                                  (scopes: Permission*)
                                                                  (doc: Operation => Operation)
                                                                  (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "patch")(createOperation(scopes)(doc))
    patch(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
  }

  def headWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                 name: String = "",
                                                                 admin: Boolean = false,
                                                                 routeIndex: Option[RouteIndex] = None)
                                                                 (scopes: Permission*)
                                                                 (doc: Operation => Operation)
                                                                 (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "head")(createOperation(scopes)(doc))
    head(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
  }

  def deleteWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                   name: String = "",
                                                                   admin: Boolean = false,
                                                                   routeIndex: Option[RouteIndex] = None)
                                                                   (scopes: Permission*)
                                                                   (doc: Operation => Operation)
                                                                   (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "delete")(createOperation(scopes)(doc))
    delete(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
  }

  def optionsWithPermission[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                    name: String = "",
                                                                    admin: Boolean = false,
                                                                    routeIndex: Option[RouteIndex] = None)
                                                                    (scopes: Permission*)
                                                                    (doc: Operation => Operation)
                                                                    (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "options")(createOperation(scopes)(doc))
    options(route, name, admin, routeIndex)((createCallback(scopes.toSeq)(callback)))
  }

  protected def createOperation(scopes: Seq[Permission])(doc: Operation => Operation) = {
    val doc2: Operation => Operation = { o =>
      val o2 = doc(o)
      if (scopes.size > 0) {
        o2.addSecurity("accessCode", scopes.map(_.value).asJava)
        o2.addSecurity("application", scopes.map(_.value).asJava)
      }
      o2.addSecurity("api_key", List[String]().asJava)
      o2
    }
    doc2
  }

  protected def createCallback[RequestType: Manifest, ResponseType: Manifest](scopes: Seq[Permission])(callback: RequestType => ResponseType) = {
    if (scopes.size > 0) {
      val createCallback: RequestType => ResponseType = { request =>
        if (validatePermission(scopes.toList) == true) {
          callback(request)
        } else {
          throw CactaceaException(OperationNotAllowed)
        }
      }
      createCallback
    } else {
      callback
    }
  }

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

private class PermissionRouteDSLWrapper(protected val dsl: RouteDSL)(implicit protected val swagger: Swagger) extends PermissionRouteDSL
