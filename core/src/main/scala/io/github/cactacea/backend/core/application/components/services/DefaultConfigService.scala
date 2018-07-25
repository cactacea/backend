package io.github.cactacea.backend.core.application.components.services

import io.github.cactacea.backend.core.application.components.interfaces.ConfigService
import io.github.cactacea.backend.core.domain.enums.DeviceType


class DefaultConfigService(
                            val apiKeys: List[(DeviceType, String)],
                            val signingKey: String,
                            val expire: Int,
                            val issuer: String,
                            val subject: String,
                            val algorithm: String,
                            val maximumGroupAccountLimits: Long,
                            val basePointInTime: Long,
                            val numberOfShards: Long
                          ) extends ConfigService
