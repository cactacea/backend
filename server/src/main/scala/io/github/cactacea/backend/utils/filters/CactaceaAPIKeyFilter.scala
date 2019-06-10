package io.github.cactacea.backend.utils.filters

import com.google.inject.Singleton
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.utils.auth.{CactaceaAPIKeyValidator, CactaceaContext}

@Singleton
class CactaceaAPIKeyFilter extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val apiKey = request.headerMap.get(Config.auth.headerNames.apiKey)
    CactaceaAPIKeyValidator.check(apiKey).flatMap({ deviceType =>
      CactaceaContext.setDeviceType(deviceType)
      service(request)
    })
  }

}
