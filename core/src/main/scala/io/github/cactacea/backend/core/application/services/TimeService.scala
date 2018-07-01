package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import io.github.cactacea.backend.core.application.components.interfaces.ConfigService

@Singleton
class TimeService @Inject()(configService: ConfigService) {

  def nanoTime(): Long = {
    System.currentTimeMillis
  }

}
