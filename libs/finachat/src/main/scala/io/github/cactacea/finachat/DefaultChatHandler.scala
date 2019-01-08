package io.github.cactacea.finachat

import com.twitter.util.Future

class DefaultChatHandler extends ChatHandler[AuthInfo] {

  override def connect(authInfo: String): Future[Option[AuthInfo]] = {
    Future.value(Some(DefaultAuthInfo(authInfo)))
  }

  override def disconnect(authInfo: AuthInfo): Future[Unit] = {
    Future.Done
  }

  override def join(authInfo: AuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def leave(authInfo: AuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def send(authInfo: AuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def joinMessage(authInfo: AuthInfo): Future[String] = {
    Future.value(s"${authInfo.displayName} has joined.")
  }

  override def leaveMessage(authInfo: AuthInfo): Future[String] = {
    Future.value(s"${authInfo.displayName} has left.")
  }

  override def sendMessage(authInfo: AuthInfo, message: String): Future[String] = {
    Future.value(message)
  }

}

