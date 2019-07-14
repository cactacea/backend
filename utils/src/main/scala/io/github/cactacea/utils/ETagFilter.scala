package io.github.cactacea.utils

import com.twitter.finagle.{Service, SimpleFilter, http}
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.io.Buf.ByteArray.Owned.extract
import com.twitter.util.Future
import com.roundeights.hasher.Implicits._
import io.netty.handler.codec.http.HttpHeaderNames._

class ETagFilter extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    service(request).map({response =>
      if (request.method == Method.Get && response.statusCode == 200) {
        val tag = extract(response.content).md5.hex
        if (request.headerMap.get(IF_NONE_MATCH.toString) == Some(tag)) {
          Response(request.version, http.Status.NotModified)
        } else {
          response.headerMap(ETAG.toString) = tag
          response
        }
      } else {
        response
      }
    })
  }

  def md5(text: String): String = {
    java.security.MessageDigest.getInstance("MD5").digest(text.getBytes).map("%02x".format(_)).mkString
  }

}
