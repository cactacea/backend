package io.github.cactacea.finasocket

import java.net.{SocketAddress, URI}

import com.twitter.concurrent.AsyncStream

import scala.collection.immutable

case class Request(
    uri: URI,
    headers: immutable.Map[String, String],
    remoteAddress: SocketAddress,
    messages: AsyncStream[Frame])
