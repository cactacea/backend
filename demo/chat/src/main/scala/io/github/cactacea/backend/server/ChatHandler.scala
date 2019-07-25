package io.github.cactacea.backend.server

import com.twitter.util.Future
import io.github.cactacea.finachat.{ChatHandler => BaseChatHandler}

class ChatHandler extends BaseChatHandler[ChatAuthInfo] {

  override def connect(authInfo: String): Future[Option[ChatAuthInfo]] = {
    Future.value(Some(new ChatAuthInfo()))
  }

  override def disconnect(authInfo: ChatAuthInfo): Future[Unit] = {
    Future.Done
  }

  override def canJoin(authInfo: ChatAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def canLeave(authInfo: ChatAuthInfo, room: String): Future[Boolean] = {
    Future.True
  }

  override def canSend(authInfo: ChatAuthInfo, room: String): Future[Boolean] = {
    Future.False
  }

  override def joinMessage(authInfo: ChatAuthInfo, room: String): Future[Option[String]] = {
    Future.None
  }


  override def leaveMessage(authInfo: ChatAuthInfo, room: String): Future[Option[String]] = {
    Future.None
  }

  override def sendMessage(authInfo: ChatAuthInfo, room: String, message: String): Future[Option[String]] = {
    Future.None
  }

}
