package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.components.services.DefaultConfigService

object DefaultConfigModule extends TwitterModule {

  val maxGroupAccountsCount = flag(name = "maxGroupAccountsCount", default = "0", help = "auth token expire")

  override def configure(): Unit = {
    maxGroupAccountsCount.get match {
      case Some(m) =>
        bindSingleton[ConfigService].toInstance(new DefaultConfigService(m.toLong))
      case None =>
        bindSingleton[ConfigService].toInstance(new DefaultConfigService(0L))
    }
  }

}
