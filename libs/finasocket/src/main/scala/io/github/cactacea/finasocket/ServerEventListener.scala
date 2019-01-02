package io.github.cactacea.finasocket

trait ServerEventListener {
  def onOpen(clientSocket: ClientSocket): Unit
  def onClose(clientSocket: ClientSocket): Unit
}
