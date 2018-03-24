package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future

trait IdentifyService {

  def generate(): Future[Long]

}
