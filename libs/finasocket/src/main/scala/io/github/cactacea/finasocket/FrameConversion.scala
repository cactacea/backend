package io.github.cactacea.finasocket

import java.net.URI

import io.github.cactacea.finasocket.Frame.{Binary, Ping, Pong, Text}
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.websocketx._

object FrameConversion {
  import ByteBufConversion._

  def fromNetty(m: Any): Frame = m match {
    case text: TextWebSocketFrame => Text(text.text())
    case cont: ContinuationWebSocketFrame => Text(cont.text())
    case binary: BinaryWebSocketFrame => Binary(byteBufAsBuf(binary.content()))
    case ping: PingWebSocketFrame => Binary(byteBufAsBuf(ping.content()))
    case pong: PongWebSocketFrame => Binary(byteBufAsBuf(pong.content()))
    case frame =>
      throw new IllegalStateException(s"unknown frame: $frame")
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
    WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, httpHeaders, 1280000)
  }


}
