package io.github.cactacea.finachat

import com.twitter.util.Future

trait ChatHandler[A <: AuthInfo] {

  def connect(authInfo: String): Future[Option[A]]
  def disconnect(authInfo: A): Future[Unit]
  def canJoin(authInfo: A, room: String): Future[Boolean]
  def canLeave(authInfo: A, room: String): Future[Boolean]
  def canSend(authInfo: A, room: String): Future[Boolean]
  def joinMessage(authInfo: A, room: String): Future[Option[AnyRef]]
  def leaveMessage(authInfo: A, room: String): Future[Option[AnyRef]]
  def sendMessage(authInfo: A, room: String, message: String): Future[Option[AnyRef]]

}
