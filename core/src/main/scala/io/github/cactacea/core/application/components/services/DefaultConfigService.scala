package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import io.github.cactacea.core.application.components.interfaces.ConfigService

@Singleton
case class DefaultConfigService (
                                  tokenExpire: Long,
                                  tokenSubject: String,
                                  tokenIssuer: String,
                                  maxGroupCount: Long,
                                  maxGroupAccountCount: Long
) extends ConfigService
