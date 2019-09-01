package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId

trait ChatService {

  def publish(channelId: ChannelId, message: AnyRef): Future[Unit]

}
