package io.github.cactacea.core.application.components.interfaces

trait ConfigService {
  val tokenExpire: Long
  val tokenSubject: String
  val tokenIssuer: String
  val maxGroupCount: Long
  val maxGroupAccountCount: Long
}
