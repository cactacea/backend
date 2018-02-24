package io.github.cactacea.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.infrastructure.queues.{NoQueueService, QueueService, SQSService}

object QueueServiceProviderModule extends TwitterModule {

  val env = flag(name = "queue", default = "none", help = "none or sqs")

  override def configure() {
    env() match {
      case "sqs" =>
        bindSingleton[QueueService].to(classOf[SQSService])
      case _ =>
        bindSingleton[QueueService].to(classOf[NoQueueService])
    }
  }

}
