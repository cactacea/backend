package io.github.cactacea.backend

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.BaseModules
import io.github.cactacea.backend.core.application.components.modules.DefaultJacksonModule
import io.github.cactacea.backend.utils.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}

trait BaseServer extends HttpServer with BaseModules {

  override def jacksonModule: TwitterModule = DefaultJacksonModule

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
  }

}

