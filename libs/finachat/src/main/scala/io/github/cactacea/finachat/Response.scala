package io.github.cactacea.finachat

import io.github.cactacea.finasocket.Frame

case class Response(result: Int, message: Option[AnyRef], errors: Option[Array[ResponseError]])

object Response {

  val Done: Frame = {
    val response = Response(0, None, None)
    val text = ObjectMapper.write(response)
    Frame.Text(text)
  }

  val InvalidCommand: Frame  = error(90000, "Command is invalid.")
  val AlreadyJoinedRoom: Frame  = error(90001, "Already joined room.")
  val NotJoinedRoom: Frame  = error(90002, "Not joined room.")
  val CouldNotLogin: Frame  = error(90003, "Could not login.")
  val CouldNotJoin: Frame = error(90004, "Could not join.")
  val CouldNotLeave: Frame = error(90005, "Could not leave.")
  val CouldNotSend: Frame = error(90006, "Could not send.")

  private def error(code: Int, message: String): Frame = {
    val error = ResponseError(code.toString, message)
    val response = Response(-1, None, Some(Array(error)))
    val text = ObjectMapper.write(response)
    Frame.Text(text)
  }

}