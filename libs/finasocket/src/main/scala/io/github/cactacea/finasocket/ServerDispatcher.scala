package io.github.cactacea.finasocket

import java.net.URI

import com.twitter.concurrent.AsyncStream
import com.twitter.finagle.Service
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.finagle.transport.Transport
import com.twitter.util.{Closable, Future, Promise, Time}
import io.netty.channel.Channel
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame

import scala.collection.JavaConverters._

class ServerDispatcher(
                        trans: Transport[Any, Any],
                        service: Service[Client, Client],
                        stats: StatsReceiver)
  extends Closable {

  import com.twitter.finagle.websocket.Netty4.{fromNetty, toNetty}

  private[this] def messages(closer: Promise[Unit]): AsyncStream[Frame] =
    AsyncStream.fromFuture(trans.read()).flatMap {
      case _: CloseWebSocketFrame => {
        closer.setValue()
        AsyncStream.empty
      }
      case frame =>
        fromNetty(frame) +:: messages(closer)
    }

  // The first item is a HttpRequest.
  trans.read().flatMap {
    case (req: HttpRequest, channel: Channel) =>
      val uri = new URI(req.uri())
      val headers = req.headers.asScala.map(e => e.getKey -> e.getValue).toMap
      val closer = new Promise[Unit]
      val close = () => {
        trans.close()
        closer.setValue()
      }
      val onRead = messages(closer)
      // Echo client
      val request = Client(uri, channel, headers, onRead, onRead, closer, close)
      service(request).flatMap { response =>
        response.onWrite
          .map(toNetty)
          .foreachF(trans.write)
          .ensure(trans.close())
      }
    case _ =>
      trans.close()
  }

  def close(deadline: Time): Future[Unit] = trans.close()
}
