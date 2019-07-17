package io.github.cactacea.backend.utils.filters

import com.google.inject.Singleton
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.APIKeyIsInValid
import io.github.cactacea.backend.utils.context.CactaceaContext

@Singleton
class CactaceaAPIKeyFilter extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val apiKey = request.headerMap.get(Config.auth.headerNames.apiKey)
    check(apiKey).flatMap({ deviceType =>
      CactaceaContext.setDeviceType(deviceType)
      service(request)
    })
  }

  def check(apiKey: Option[String]): Future[DeviceType] = {
    apiKey.flatMap(key => Config.auth.keys.all.filter({ case (_, k) => k == key}).headOption.map({ case (d, _) => d})) match {
      case Some(t) =>
        Future.value(t)
      case None =>
        Future.exception(CactaceaException(APIKeyIsInValid))
    }
  }

}
