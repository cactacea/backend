package io.github.cactacea.finasocket

import com.twitter.finagle.transport.Transport

case class ClientSocket(transport: Transport[Any, Any]) {
}
