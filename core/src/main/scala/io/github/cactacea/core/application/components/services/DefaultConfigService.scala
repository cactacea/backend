package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import io.github.cactacea.core.application.components.interfaces.ConfigService

@Singleton
class DefaultConfigService(val maxGroupAccountsCount: Long) extends ConfigService

