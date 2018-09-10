package io.github.cactacea.backend.core.util.configs

case class PasswordConfig (
                            salt: Option[String],
                            iterations: Option[Int],
                            keyLength: Option[Int]
                          )
