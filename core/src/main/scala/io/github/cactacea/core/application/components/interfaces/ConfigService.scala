package io.github.cactacea.core.application.components.interfaces

import io.github.cactacea.core.domain.enums.DeviceType

trait ConfigService {
  val rootPath: String
  val apiKeys: List[(DeviceType, String)]
  val signingKey: String
  val expire: Long
  val issuer: String
  val subject: String
  val algorithm: String
  val maximumGroupAccountLimits: Long
  val basePointInTime: Long
}
