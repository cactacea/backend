package io.github.cactacea.finachat

import com.twitter.util.Future

trait ChatHandler[A <: AuthInfo] {

  def connect(authInfo: String): Future[Option[A]]
  def disconnect(authInfo: A): Future[Unit]
  def join(authInfo: A, room: String): Future[Boolean]
  def leave(authInfo: A, room: String): Future[Boolean]
  def send(authInfo: A, room: String): Future[Boolean]
  def joinMessage(authInfo: A): Future[String]
  def leaveMessage(authInfo: A): Future[String]
  def sendMessage(authInfo: A, message: String): Future[String]

}
