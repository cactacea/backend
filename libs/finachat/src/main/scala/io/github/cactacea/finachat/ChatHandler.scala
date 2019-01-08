package io.github.cactacea.finachat

import com.twitter.util.Future

trait ChatHandler[A <: AuthInfo] {

  def connect(authInfo: String): Future[Option[AuthInfo]]
  def disconnect(authInfo: AuthInfo): Future[Unit]
  def join(authInfo: AuthInfo, room: String): Future[Boolean]
  def leave(authInfo: AuthInfo, room: String): Future[Boolean]
  def send(authInfo: AuthInfo, room: String): Future[Boolean]
  def joinMessage(authInfo: AuthInfo): Future[String]
  def leaveMessage(authInfo: AuthInfo): Future[String]
  def sendMessage(authInfo: AuthInfo, message: String): Future[String]

}
