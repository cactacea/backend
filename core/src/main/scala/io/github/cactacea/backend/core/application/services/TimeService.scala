package io.github.cactacea.backend.core.application.services

import com.google.inject.Singleton

@Singleton
class TimeService {

  def nanoTime(): Long = {
    System.currentTimeMillis
  }

}
