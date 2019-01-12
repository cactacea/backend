package io.github.cactacea.backend

import com.twitter.util.Future
import io.github.cactacea.finachat.{ChatHandler => BaseChatHandler}

class ChatHandler extends BaseChatHandler[ChatAuthInfo] {

  override def connect(authInfo: String): Future[Option[ChatAuthInfo]] = {
    Future.value(Some(new ChatAuthInfo()))
  }

  override def disconnect(authInfo: ChatAuthInfo): Future[Unit] = {
    Future.Done
  }

  override def join(authInfo: ChatAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def leave(authInfo: ChatAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def send(authInfo: ChatAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def joinMessage(authInfo: ChatAuthInfo): Future[String] = {
    Future.value("New member has joined.")
  }

  override def leaveMessage(authInfo: ChatAuthInfo): Future[String] = {
    Future.value("New member has left.")
  }

  override def sendMessage(authInfo: ChatAuthInfo, message: String): Future[String] = {
    Future.value(message)
  }

}
