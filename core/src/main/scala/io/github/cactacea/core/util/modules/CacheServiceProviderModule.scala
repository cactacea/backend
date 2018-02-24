package io.github.cactacea.core.util.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.infrastructure.caches.{CacheService, NoCacheService, RedisService}

object CacheServiceProviderModule extends TwitterModule {

  val env = flag(name = "cache", default = "none", help = "none or redis")

  override def configure() {
    env() match {
      case "redis" =>
        bindSingleton[CacheService].to(classOf[RedisService])
      case _ =>
        bindSingleton[CacheService].to(classOf[NoCacheService])
    }
  }

}
