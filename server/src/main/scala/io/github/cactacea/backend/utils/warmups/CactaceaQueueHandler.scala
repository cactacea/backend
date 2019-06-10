package io.github.cactacea.backend.utils.warmups

import com.google.inject.{Inject, Singleton}
import com.twitter.inject.utils.Handler
import io.github.cactacea.backend.core.application.components.interfaces.QueueService

@Singleton
class CactaceaQueueHandler @Inject()(queueService: QueueService) extends Handler {

  override def handle(): Unit = {
    queueService.start()
  }

}
