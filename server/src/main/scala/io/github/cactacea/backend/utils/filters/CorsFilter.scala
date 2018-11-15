package io.github.cactacea.backend.utils.filters

import com.google.inject.Singleton
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.http.filter.Cors.HttpFilter
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

@Singleton
class CorsFilter extends SimpleFilter[Request, Response] {
  val allowsOrigin  = { origin: String => Some("http://localhost:4200") }
  val allowsMethods = { method: String => Some(Seq("GET", "POST", "PUT", "DELETE", "OPTIONS")) }
  val allowsHeaders = { headers: Seq[String] => Some(Seq("Origin","Authorization","Accept", "Content-Type")) }

  val policy = Cors.Policy(allowsOrigin, allowsMethods, allowsHeaders, supportsCredentials = true)
  val cors = new HttpFilter(policy)

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    cors.apply(request, service)
  }
}