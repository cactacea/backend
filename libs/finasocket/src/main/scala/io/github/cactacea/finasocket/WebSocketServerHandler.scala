package io.github.cactacea.finasocket

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http.{HttpHeaderNames, HttpRequest}
import io.netty.handler.codec.http.websocketx._

class WebSocketServerHandler extends ChannelInboundHandlerAdapter {
  private[this] var handshaker: Option[WebSocketServerHandshaker] = None

  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
    msg match {
      case http: HttpRequest =>
        println("Got HTTP Request")
        handleHttpRequest(ctx, http)
      case frame: WebSocketFrame =>
        println("Got Websocket Frame")
        handleWebsocketFrame(ctx, frame)
    }
  }

  private def handleHttpRequest(ctx: ChannelHandlerContext, request: HttpRequest): Unit = {
    val scheme = if(request.uri().startsWith("wss")) "wss" else "ws"
    val location = scheme + "://" + request.headers.get(HttpHeaderNames.HOST) + "/"
    val wsFactory = new WebSocketServerHandshakerFactory(location, null, false)
    handshaker = Option(wsFactory.newHandshaker(request))
    handshaker match {
      case None =>
        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel())
      case Some(ref) =>
        ref.handshake(ctx.channel(), request)
        ctx.fireChannelRead(request)
    }
  }

  private def handleWebsocketFrame(ctx: ChannelHandlerContext, frame: WebSocketFrame): Unit = {
    frame match {
      case frame: CloseWebSocketFrame =>
        handshaker match {
          case Some(hs) =>
            hs.close(ctx.channel(), frame.retain()).addListener(ChannelFutureListener.CLOSE)
            ctx.fireChannelRead(frame)
          case None =>
            ctx.fireExceptionCaught(new IllegalArgumentException(s"Close received before handshake"))
        }
      case frame: WebSocketFrame =>
        ctx.fireChannelRead(frame)
      case invalid =>
        ctx.fireExceptionCaught(new IllegalArgumentException(s"invalid message: $invalid"))
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
