package io.cactacea.finagger

import com.twitter.inject.TwitterModule

abstract class SwaggerModule extends TwitterModule {
  flag(name = "swagger.docs.endpoint", default = "/docs", help = "The key to use")
}
