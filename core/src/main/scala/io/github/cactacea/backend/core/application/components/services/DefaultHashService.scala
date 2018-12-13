package io.github.cactacea.backend.core.application.components.services

import com.roundeights.hasher.Implicits._
import io.github.cactacea.backend.core.application.components.interfaces.HashService
import io.github.cactacea.backend.core.util.configs.Config

class DefaultHashService extends HashService {

  override def hash(plain: String): String = {
    plain.pbkdf2(Config.password.salt, Config.password.iterations, Config.password.keyLength)
  }

}
