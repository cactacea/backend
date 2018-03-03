package io.github.cactacea.core.application.components.services

import io.github.cactacea.core.application.components.interfaces.MessageService

class DefaultMessageService extends MessageService {

  def postFeed(displayName: String): String = {
    "{0} フィードを投稿しました。".format(displayName)
  }

}
