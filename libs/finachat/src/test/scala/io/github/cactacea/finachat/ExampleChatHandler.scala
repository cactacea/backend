package io.github.cactacea.finachat

import com.twitter.util.Future

class ExampleChatHandler extends ChatHandler[ExampleAuthInfo] {

  override def connect(authInfo: String): Future[Option[ExampleAuthInfo]] = {
    Future.value(Some(ExampleAuthInfo(authInfo)))
  }

  override def disconnect(authInfo: ExampleAuthInfo): Future[Unit] = {
    Future.Done
  }

  override def join(authInfo: ExampleAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def leave(authInfo: ExampleAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def send(authInfo: ExampleAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def joinMessage(authInfo: ExampleAuthInfo): Future[String] = {
    Future.value(s"${authInfo.displayName} has joined.")
  }

  override def leaveMessage(authInfo: ExampleAuthInfo): Future[String] = {
    Future.value(s"${authInfo.displayName} has left.")
  }

  override def sendMessage(authInfo: ExampleAuthInfo, message: String): Future[String] = {
    Future.value(message)
  }

}

