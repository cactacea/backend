package io.github.cactacea.backend.core.application.components.interfaces

import io.github.cactacea.backend.core.domain.enums.DeviceType

trait ConfigService {
  val apiKeys: List[(DeviceType, String)]
  val signingKey: String
  val expire: Int
  val issuer: String
  val subject: String
  val algorithm: String
  val maximumGroupAccountLimits: Long
  val numberOfShards: Long
}
