package io.github.cactacea.backend.server.utils.modules

import com.twitter.inject.TwitterModule

object APIPrefixModule extends TwitterModule {

  flag(name = "cactacea.api.prefix", default = "/", help = "Cactacea Api endpoint prefix")

}