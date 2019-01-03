package io.github.cactacea.finasocket

import java.net.URI

import com.twitter.concurrent.AsyncStream
import com.twitter.finagle.Service
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.finagle.transport.Transport
import com.twitter.util.{Closable, Future, Time}
import io.netty.channel.Channel
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame

import scala.collection.JavaConverters._

class ServerDispatcher(
  trans: Transport[Any, Any],
  service: Service[Request, Response],
  stats: StatsReceiver)
  extends Closable {

  import FrameConversion.{fromNetty, toNetty}

  private[this] def messages(): AsyncStream[Frame] =
    AsyncStream.fromFuture(trans.read()).flatMap {
      case _: CloseWebSocketFrame => AsyncStream.empty
      case frame => fromNetty(frame) +:: messages()
    }

  // The first item is a HttpRequest.
  trans.read().flatMap {
    case (req: HttpRequest) =>
      val uri = new URI(req.uri())
      val headers = req.headers.asScala.map(e => e.getKey -> e.getValue).toMap
      val url = Request(uri, headers, trans.context.remoteAddress, messages())
      service(url).flatMap { response =>
        response.messages
          .map(toNetty)
          .foreachF(trans.write)
          .ensure(trans.close())
      }
    case _ => trans.close()
  }

  def close(deadline: Time): Future[Unit] = {
    trans.close()
  }
}
