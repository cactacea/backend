package io.github.cactacea.finachat

import com.twitter.inject.annotations.Lifecycle

trait ChatServer extends BaseChatServer {

  @Lifecycle
  override protected def postInjectorStartup(): Unit = {
    super.postInjectorStartup()
  }

}
