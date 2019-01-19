package io.github.cactacea.finasocket

import com.twitter.concurrent.AsyncStream

case class Response(onReceived: AsyncStream[Frame])
