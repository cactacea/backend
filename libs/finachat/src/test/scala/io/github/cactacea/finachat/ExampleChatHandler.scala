package io.github.cactacea.finachat

import com.twitter.util.Future

class ExampleChatHandler extends ChatHandler[ExampleAuthInfo] {

  override def connect(authInfo: String): Future[Option[ExampleAuthInfo]] = {
    Future.value(Some(ExampleAuthInfo(authInfo)))
  }

  override def disconnect(authInfo: ExampleAuthInfo): Future[Unit] = {
    Future.Done
  }

  override def canJoin(authInfo: ExampleAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def canLeave(authInfo: ExampleAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def canSend(authInfo: ExampleAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def joinMessage(authInfo: ExampleAuthInfo, room: String): Future[Option[String]] = {
    Future.value(Some(s"${authInfo.displayName} has joined."))
  }

  override def leaveMessage(authInfo: ExampleAuthInfo, room: String): Future[Option[String]] = {
    Future.value(Some(s"${authInfo.displayName} has left."))
  }

  override def sendMessage(authInfo: ExampleAuthInfo, room: String, message: String): Future[Option[String]] = {
    Future.value(Some(message))
  }

}

