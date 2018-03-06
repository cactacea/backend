package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import io.github.cactacea.core.application.components.interfaces.ConfigService

@Singleton
class DefaultConfigService(
                            val apiKey: String,
                            val signingKey: String,
                            val expire: Long,
                            val issuer: String,
                            val subject: String,
                            val algorithm: String,
                            val maxGroupAccountsCount: Long
                          ) extends ConfigService
