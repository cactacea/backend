package io.github.cactacea.finachat

case class Response(responseType: ResponseType, message: Option[AnyRef], errors: Option[Array[ResponseError]])

object Response {

  val done: String = {
    create(ResponseType.operationSucceed, None)
  }

  def memberJoined(message: Option[AnyRef]): String = {
    create(ResponseType.memberJoined, message)
  }

  def memberLeft(message: Option[AnyRef]): String = {
    create(ResponseType.memberLeft, message)
  }

  def messageArrived(message: Option[AnyRef]): String = {
    create(ResponseType.messageArrived, message)
  }

  val invalidCommand: String  = create(90000, "Command is invalid.")
  val alreadyJoinedRoom: String  = create(90001, "Already joined room.")
  val notJoinedRoom: String  = create(90002, "Not joined room.")
  val couldNotLogin: String  = create(90003, "Could not login.")
  val couldNotJoin: String = create(90004, "Could not join.")
  val couldNotLeave: String = create(90005, "Could not leave.")
  val couldNotSend: String = create(90006, "Could not send.")

  private def create(responseType: ResponseType, body: Option[AnyRef]): String = {
    val response = Response(responseType, body, None)
    ObjectMapper.write(response)
  }

  private def create(errorCode: Int, errorMessage: String): String = {
    val error = ResponseError(errorCode.toString, errorMessage)
    val response = Response(ResponseType.operationFailed, None, Some(Array(error)))
    ObjectMapper.write(response)
  }

}