package io.github.cactacea.backend.utils.filters

import com.google.inject.Singleton
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter, http}
import com.twitter.util.Future

@Singleton
class CorsFilter extends SimpleFilter[Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
   service(request).map {
      response =>
        if (request.method.name == "OPTIONS") {
          response.statusCode(http.Status.Ok.code)
        } else {
          response.headerMap
            .set("Access-Control-Allow-Credentials", "true")
            .set("Access-Control-Allow-Headers", "Origin, Authorization, Accept")
            .set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
            .set("Access-Control-Allow-Origin", "*")
          response
        }
    }
  }
}