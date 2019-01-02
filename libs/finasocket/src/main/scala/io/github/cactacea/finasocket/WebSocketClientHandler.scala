package io.github.cactacea.finasocket

import io.netty.channel._
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx._

class WebSocketClientHandler(handshaker:WebSocketClientHandshaker)
  extends ChannelInboundHandlerAdapter {

  import io.netty.channel.ChannelHandlerContext

  var handshakeFuture:ChannelPromise = _

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    handshaker.handshake(ctx.channel())
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
  }

  override def handlerAdded(ctx:ChannelHandlerContext):Unit = {
    handshakeFuture = ctx.newPromise()
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
    val ch = ctx.channel()
    if(! handshaker.isHandshakeComplete){
      handshaker.finishHandshake(ch, msg.asInstanceOf[FullHttpResponse])
      handshakeFuture.setSuccess()
    }
    super.channelRead(ctx, msg)
  }

  override def exceptionCaught(ctx:ChannelHandlerContext, cause:Throwable):Unit = {
    cause.printStackTrace()
    ctx.close()
  }

}