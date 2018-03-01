package io.github.cactacea.core.application.components.interfaces

trait MessageService {

  def postFeed(displayName: String): String
}
