package io.github.cactacea.core.application.components.services

import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.domain.enums.DeviceType


class DefaultConfigService(
                            val rootPath: String,
                            val apiKeys: List[(DeviceType, String)],
                            val signingKey: String,
                            val expire: Long,
                            val issuer: String,
                            val subject: String,
                            val algorithm: String,
                            val maximumGroupAccountLimits: Long,
                            val basePointInTime: Long
                          ) extends ConfigService
