package io.github.cactacea.backend.server

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.BaseModules
import io.github.cactacea.backend.core.application.components.modules.DefaultJacksonModule
import io.github.cactacea.backend.server.utils.mappers.{CactaceaCaseClassExceptionMapper, CactaceaExceptionMapper}

trait BaseServer extends HttpServer with BaseModules {

  override def jacksonModule: TwitterModule = DefaultJacksonModule

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .exceptionMapper[CactaceaCaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
  }

}
