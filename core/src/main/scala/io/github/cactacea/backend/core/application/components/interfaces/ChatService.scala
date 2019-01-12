package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId

trait ChatService {

  def publish(groupId: GroupId, message: String): Future[Unit]

}
