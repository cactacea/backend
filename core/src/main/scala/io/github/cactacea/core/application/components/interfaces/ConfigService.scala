package io.github.cactacea.core.application.components.interfaces

trait ConfigService {
  val apiKey: String
  val signingKey: String
  val expire: Long
  val issuer: String
  val subject: String
  val algorithm: String
  val maximumGroupAccountLimits: Long
  val basePointInTime: Long
}
