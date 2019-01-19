package com.twitter.finagle.websocket

import java.net.{SocketAddress, URI}

import com.twitter.finagle.client.Transporter
import com.twitter.finagle.netty4.{ByteBufConversion, Netty4Listener, Netty4Transporter}
import com.twitter.finagle.server.Listener
import com.twitter.finagle.transport.TransportContext
import com.twitter.finagle.{Address, Stack}
import io.github.cactacea.finasocket.Frame.{Binary, Ping, Pong, Text}
import io.github.cactacea.finasocket.{Frame, WebSocketClientHandler, WebSocketServerHandler}
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.http.websocketx._
import io.netty.handler.codec.http.{DefaultHttpHeaders, HttpClientCodec, HttpObjectAggregator, HttpServerCodec}

object Netty4 {
  import ByteBufConversion._

  private def serverPipeline(pipeline: ChannelPipeline): Unit = {
    pipeline.addLast("server", new HttpServerCodec)
    pipeline.addLast("aggregator", new HttpObjectAggregator(65536))
    pipeline.addLast("ws", new WebSocketServerHandler)
  }

  private def clientPipeline(handler: WebSocketClientHandler)(pipeline: ChannelPipeline): Unit = {
    pipeline.addLast("client", new HttpClientCodec())
    pipeline.addLast("aggregator", new HttpObjectAggregator(65536))
    pipeline.addLast("ws", handler)
  }

  def newListener(params: Stack.Params): Listener[Any, Any, TransportContext] =
    Netty4Listener(serverPipeline, params)

    def newTransporter(
                        wsScheme: String,
                        addr: SocketAddress,
                        params: Stack.Params): Transporter[Any, Any, TransportContext] = {

      val endpoint = params[Transporter.EndpointAddr].addr
      val uri = endpoint match {
        case Address.Inet(a, _) =>
          Some(new URI(s"${wsScheme}://${a.getHostName}:${a.getPort}"))
        case _ =>
          None
      }

      val httpHeaders = new DefaultHttpHeaders()
      val handShaker = WebSocketClientHandshakerFactory.newHandshaker(uri.get, WebSocketVersion.V13, null, false, httpHeaders, 1280000)
      val handler = new WebSocketClientHandler(handShaker)

      Netty4Transporter.raw(clientPipeline(handler), addr, params)

    }

  def fromNetty(m: Any): Frame = m match {
    case text: TextWebSocketFrame => Text(text.text())
    case cont: ContinuationWebSocketFrame => Text(cont.text())
    case binary: BinaryWebSocketFrame => Binary(byteBufAsBuf(binary.content()))
    case ping: PingWebSocketFrame => Binary(byteBufAsBuf(ping.content()))
    case pong: PongWebSocketFrame => Binary(byteBufAsBuf(pong.content()))
    case frame => throw new IllegalStateException(s"unknown frame: $frame")
  }

  def toNetty(frame: Frame): WebSocketFrame = frame match {
    case Text(message) => new TextWebSocketFrame(message)
    case Binary(buf) => new BinaryWebSocketFrame(bufAsByteBuf(buf))
    case Ping(buf) => new PingWebSocketFrame(bufAsByteBuf(buf))
    case Pong(buf) => new PongWebSocketFrame(bufAsByteBuf(buf))
  }

  def newHandshaker(uri: URI, headers: Map[String, String]): WebSocketClientHandshaker = {
    val httpHeaders = new DefaultHttpHeaders()
    headers.foreach({ case (k, v) => httpHeaders.add(k, v) })
    WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, httpHeaders)
  }
}
