package io.github.cactacea.finachat

import com.twitter.finagle.Service
import com.twitter.util.Future
import io.github.cactacea.finasocket.Client

/*** A null Service.  Useful for testing. */
class NullService[REQUEST <: Client] extends Service[REQUEST, Client] {
  def apply(request: REQUEST): Future[Client] =
    Future.value(request)
}

object NullService extends NullService[Client]
