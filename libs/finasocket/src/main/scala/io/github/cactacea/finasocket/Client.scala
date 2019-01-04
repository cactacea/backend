package io.github.cactacea.finasocket


import java.net.URI

import com.twitter.concurrent.AsyncStream
import com.twitter.util.{Future, Promise}
import io.netty.channel.Channel

import scala.collection.immutable

case class Client(
                   uri: URI,
                   headers: immutable.Map[String, String],
                   onRead: AsyncStream[Frame],
                   channel: Channel,
                   onClose: Future[Unit] = new Promise[Unit],
                   close: () => Unit = { () => () })

