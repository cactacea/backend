package io.github.cactacea.backend.utils.auth

import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.APIKeyIsInValid

object CactaceaAPIKeyValidator {

  def check(apiKey: Option[String]): Future[DeviceType] = {
    apiKey.flatMap(key => Config.auth.keys.all.filter({ case (_, k) => k == key}).headOption.map({ case (d, _) => d})) match {
      case Some(t) =>
        Future.value(t)
      case None =>
        Future.exception(CactaceaException(APIKeyIsInValid))
    }
  }

}
