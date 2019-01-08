package io.github.cactacea.finachat

case class Command(name: String, value: String)

object Command {

  val Connect = "connect"
  val Disconnect = "disconnect"
  val Join = "join"
  val Leave = "leave"
  val Send = "send"

}