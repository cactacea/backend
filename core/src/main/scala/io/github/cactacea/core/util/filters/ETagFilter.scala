package io.github.cactacea.core.util.filters

import com.roundeights.hasher.Implicits._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter, http}
import com.twitter.io.Buf.ByteArray.Owned.extract
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import com.twitter.finagle.http.Method

class ETagFilter extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]) = {
    service(request).map({response =>
      if (request.method == Method.Get && response.statusCode == 200) {
        val tag = extract(response.content).md5.hex
        if (request.headerMap.get(IF_NONE_MATCH) == Some(tag)) {
          Response(request.version, http.Status.NotModified)
        } else {
          response.headerMap(ETAG) = tag
          response
        }
      } else {
        response
      }
    })
  }
}
