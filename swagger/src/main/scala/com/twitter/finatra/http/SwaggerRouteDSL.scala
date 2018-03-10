package com.twitter.finatra.http

import com.jakehschwartz.finatra.swagger.FinatraSwagger
import com.twitter.finagle.http.RouteIndex
import io.swagger.models.{Operation, Swagger}

/**
  * To work around the accessibility of RouteDSL, this class is in "com.twitter.finatra.http" package
  */
object SwaggerRouteDSL {
  implicit def convert(dsl: RouteDSL)(implicit swagger: Swagger): SwaggerRouteDSL = new SwaggerRouteDSLWrapper(dsl)(swagger)
}

trait SwaggerRouteDSL extends RouteDSL {
  implicit protected val swagger: Swagger

  def postWithDoc[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                 name: String = "",
                                                                 admin: Boolean = false,
                                                                 routeIndex: Option[RouteIndex] = None)
                                                                (doc: Operation => Operation)
                                                                (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "post")(doc)
    post(route, name, admin, routeIndex)(callback)
  }

  def getWithDoc[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                               (doc: Operation => Operation)
                                                               (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "get")(doc)
    get(route, name, admin, routeIndex)(callback)
  }

  def putWithDoc[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                name: String = "",
                                                                admin: Boolean = false,
                                                                routeIndex: Option[RouteIndex] = None)
                                                               (doc: Operation => Operation)
                                                               (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "put")(doc)
    put(route, name, admin, routeIndex)(callback)
  }

  def patchWithDoc[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                  name: String = "",
                                                                  admin: Boolean = false,
                                                                  routeIndex: Option[RouteIndex] = None)
                                                                 (doc: Operation => Operation)
                                                                 (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "patch")(doc)
    patch(route, name, admin, routeIndex)(callback)
  }

  def headWithDoc[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                 name: String = "",
                                                                 admin: Boolean = false,
                                                                 routeIndex: Option[RouteIndex] = None)
                                                                (doc: Operation => Operation)
                                                                (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "head")(doc)
    head(route, name, admin, routeIndex)(callback)
  }

  def deleteWithDoc[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                   name: String = "",
                                                                   admin: Boolean = false,
                                                                   routeIndex: Option[RouteIndex] = None)
                                                                  (doc: Operation => Operation)
                                                                  (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "delete")(doc)
    delete(route, name, admin, routeIndex)(callback)
  }

  def optionsWithDoc[RequestType: Manifest, ResponseType: Manifest](route: String,
                                                                    name: String = "",
                                                                    admin: Boolean = false,
                                                                    routeIndex: Option[RouteIndex] = None)
                                                                   (doc: Operation => Operation)
                                                                   (callback: RequestType => ResponseType): Unit = {
    registerOperation(route, "options")(doc)
    options(route, name, admin, routeIndex)(callback)
  }

  private def registerOperation(path: String, method: String)(doc: Operation => Operation): Unit = {
    FinatraSwagger
      .convert(swagger)
      .registerOperation(prefixRoute(path), method, doc(new Operation))
  }


  //exact copy from Finatra RouteDSL class (it is defined as private there)
  private def prefixRoute(route: String): String = {
    contextVar().prefix match {
      case prefix if prefix.nonEmpty && prefix.startsWith("/") => s"$prefix$route"
      case prefix if prefix.nonEmpty && !prefix.startsWith("/") => s"/$prefix$route"
      case _ => route
    }
  }
}

private class SwaggerRouteDSLWrapper(protected val dsl: RouteDSL)(implicit protected val swagger: Swagger) extends SwaggerRouteDSL
