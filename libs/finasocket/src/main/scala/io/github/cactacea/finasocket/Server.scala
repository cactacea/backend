package io.github.cactacea.finasocket

import com.twitter.finagle.netty4.Netty4Listener
import com.twitter.finagle.param.{ProtocolLibrary, Stats}
import com.twitter.finagle.server.{Listener, StackServer, StdStackServer}
import com.twitter.finagle.transport.{Transport, TransportContext}
import com.twitter.finagle.{Service, ServiceFactory, Stack}
import com.twitter.util.Closable
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.http.{HttpObjectAggregator, HttpServerCodec}

case class Server(
                   stack: Stack[ServiceFactory[Request, Response]] = StackServer.newStack,
                   params: Stack.Params = StackServer.defaultParams + ProtocolLibrary("ws"),
                   eventListener: Option[ServerEventListener] = None)
  extends StdStackServer[Request, Response, Server] {

  protected type In = Any
  protected type Out = Any
  protected type Context = TransportContext

  protected def newListener(): Listener[In, Out, Context] = {
    val handler = new WebSocketServerHandler
    Netty4Listener(serverPipeline(handler), params)
  }

  private def serverPipeline(handler: WebSocketServerHandler)(pipeline: ChannelPipeline): Unit = {
    pipeline.addLast("server", new HttpServerCodec)
    pipeline.addLast("aggregator", new HttpObjectAggregator(65536))
    pipeline.addLast("ws", handler)
  }

  private[this] val statsReceiver = {
    val Stats(sr) = params[Stats]
    sr.scope("websocket")
  }

  protected def newDispatcher(
                               transport: Transport[In, Out] { type Context <: Server.this.Context },
                               service: Service[Request, Response]
                             ): Closable = {

    val dispatcher = new ServerDispatcher(transport, service, statsReceiver)


    eventListener.foreach({ listener =>
      listener.onOpen(ClientSocket(transport))
      transport.onClose ensure {
        listener.onClose(ClientSocket(transport))
      }

    })

    dispatcher
  }


  protected def copy1(
                       stack: Stack[ServiceFactory[Request, Response]] = this.stack,
                       params: Stack.Params = this.params
                     ): Server = copy(stack, params)

}
