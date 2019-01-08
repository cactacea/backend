package io.github.cactacea.finasocket


import java.net.URI

import com.twitter.concurrent.AsyncStream
import com.twitter.util.{Future, Promise}
import io.netty.channel.{Channel, ChannelFuture, ChannelFutureListener}

import scala.collection.immutable

case class Client(
                   uri: URI,
                   channel: Channel,
                   headers: immutable.Map[String, String],
                   onRead: AsyncStream[Frame],
                   onWrite: AsyncStream[Frame],
                   onClose: Future[Unit] = new Promise[Unit],
                   close: () => Unit = { () => () }) {

  private def twitterFuture(cf: ChannelFuture): Future[Channel] = {
    val p = Promise[Channel]
    cf.addListener(new ChannelFutureListener {
      override def operationComplete(f: ChannelFuture): Unit = {
        if (f.isSuccess) {
          p.setValue(cf.channel)
        } else {
          p.setException(cf.cause)
        }
      }
    })
    p
  }

}
