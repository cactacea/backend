package io.github.cactacea.backend.core.application.components.services

import com.typesafe.config.ConfigFactory
import io.github.cactacea.backend.core.application.components.interfaces.HashService

class DefaultHashService extends HashService {

  private lazy val config = ConfigFactory.load("application.conf")
  private lazy val password = config.getConfig("password")
  private lazy val salt = password.getString("salt")
  private lazy val iterations = password.getInt("iterations")
  private lazy val keyLength = password.getInt("keyLength")

  override def hash(plain: String): String = {
    import com.roundeights.hasher.Implicits._
    plain.pbkdf2(salt, iterations, keyLength)
  }

}
