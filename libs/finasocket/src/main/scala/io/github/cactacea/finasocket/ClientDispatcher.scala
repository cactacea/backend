package io.github.cactacea.finasocket

import com.twitter.concurrent.AsyncStream
import com.twitter.finagle.WriteException
import com.twitter.finagle.dispatch.GenSerialClientDispatcher
import com.twitter.finagle.stats.{NullStatsReceiver, StatsReceiver}
import com.twitter.finagle.transport.Transport
import com.twitter.util.{Future, Promise}
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame

class ClientDispatcher(trans: Transport[Any, Any], statsReceiver: StatsReceiver)
  extends GenSerialClientDispatcher[Request, Response, Any, Any](trans, statsReceiver) {

  import com.twitter.finagle.websocket.Netty4._

  def this(trans: Transport[Any, Any]) = {
    this(trans, NullStatsReceiver)
  }

  private[this] def messages(): AsyncStream[Frame] =
    AsyncStream.fromFuture(trans.read()).flatMap {
      case _: CloseWebSocketFrame => AsyncStream.empty
      case frame => fromNetty(frame) +:: messages()
    }

  protected def dispatch(req: Request, p: Promise[Response]): Future[Unit] = {

    // The first item is a HttpResponse.
    trans.read().map(_ => {
      p.setValue(Response(messages))
      req.messages
        .foreachF(msg =>
          trans.write(toNetty(msg)
          ))
        .before(
          trans.write(new CloseWebSocketFrame)
        )
        .rescue({ case exc =>
          Future.exception(WriteException(exc))
        })
    })

  }
}
