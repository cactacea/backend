package io.github.cactacea.backend.core.util.responses

import io.swagger.annotations.ApiModelProperty

case class CactaceaErrors(errors: Seq[CactaceaError])

object CactaceaErrors {

  final val c40000 = "40000"
  final val c40001 = "40001"
  final val c40002 = "40002"
  final val c40003 = "40003"
  final val c40004 = "40004"
  final val c40005 = "40005"
  final val c40006 = "40006"
  final val c40007 = "40007"
  final val c40008 = "40008"
  final val c40009 = "40009"
  final val c40010 = "40010"
  final val c40012 = "40012"
  final val c40013 = "40013"
  final val c40014 = "40014"
  final val c40015 = "40015"

  final val c40020 = "40020"
  final val c40021 = "40021"
  final val c40022 = "40022"
  final val c40023 = "40023"
  final val c40024 = "40024"
  final val c40025 = "40025"
  final val c40026 = "40026"
  final val c40027 = "40027"
  final val c40028 = "40028"
  final val c40029 = "40029"
  final val c40030 = "40030"
  final val c40031 = "40031"
  final val c40032 = "40032"
  final val c40033 = "40033"
  final val c40034 = "40034"
  final val c40035 = "40035"
  final val c40036 = "40036"
  final val c40037 = "40037"
  final val c40038 = "40038"
  final val c40039 = "40039"
  final val c40040 = "40040"
  final val c40041 = "40041"

  final val m40000 = "User not signed up."
  final val m40001 = "User deleted."
  final val m40002 = "User terminated."
  final val m40003 = "Operation not allowed."
  final val m40004 = "File upload error occurred."
  final val m40005 = "Authority not found."
  final val m40006 = "Channel is invitation only."
  final val m40007 = "Organizer can not leave."
  final val m40008 = "API Key is invalid."
  final val m40009 = "User name already used."
  final val m40010 = "Can not specify myself."
  final val m40012 = "Invalid User name or password."
  final val m40013 = "File size limit exceeded error."
  final val m40014 = "Upload media not found."
  final val m40015 = "Not acceptable mime type found."

  final val m40020 = "User already followed."
  final val m40021 = "User not followed."
  final val m40022 = "User already blocked."
  final val m40023 = "User not blocked."
  final val m40024 = "User already muted."
  final val m40025 = "User not muted."
  final val m40026 = "User already friend."
  final val m40027 = "User not friend."
  final val m40028 = "User already invited."
  final val m40029 = "User already requested."
  final val m40030 = "User already channelJoined."
  final val m40031 = "User not channelJoined."
  final val m40032 = "Comment already liked."
  final val m40033 = "Comment not liked."
  final val m40034 = "Tweet already liked."
  final val m40035 = "Tweet not liked."
  final val m40036 = "Channel already hidden."
  final val m40037 = "Channel not hidden."
  final val m40038 = "Token already expired."
  final val m40039 = "Invalid token error."
  final val m40040 = "Account already linked."
  final val m40041 = "Account not linked."

  // 400 Bad Request

  final object UserNotSignedUp extends BadRequest {
    @ApiModelProperty(example = c40000)  override val code: Int =       c40000.toInt
    @ApiModelProperty(example = m40000)  override val message: String = m40000
  }

  final object UserDeleted extends BadRequest {
    @ApiModelProperty(example = c40001)  override val code: Int =       c40001.toInt
    @ApiModelProperty(example = m40001)  override val message: String = m40001
  }

  final object UserTerminated extends BadRequest {
    @ApiModelProperty(example = c40002)  override val code: Int =       c40002.toInt
    @ApiModelProperty(example = m40002)  override val message: String = m40002
  }

  final object OperationNotAllowed extends BadRequest {
    @ApiModelProperty(example = c40003)  override val code: Int =       c40003.toInt
    @ApiModelProperty(example = m40003)  override val message: String = m40003
  }

  final object FileUploadErrorOccurred extends BadRequest {
    @ApiModelProperty(example = c40004)  override val code: Int =       c40004.toInt
    @ApiModelProperty(example = m40004)  override val message: String = m40004
  }

  final object AuthorityNotFound extends BadRequest {
    @ApiModelProperty(example = c40005)  override val code: Int =       c40005.toInt
    @ApiModelProperty(example = m40005)  override val message: String = m40005
  }

  final object InvitationChannelFound extends BadRequest {
    @ApiModelProperty(example = c40006)  override val code: Int =       c40006.toInt
    @ApiModelProperty(example = m40006)  override val message: String = m40006
  }

  final object OrganizerCanNotLeave extends BadRequest {
    @ApiModelProperty(example = c40007)  override val code: Int =       c40007.toInt
    @ApiModelProperty(example = m40007)  override val message: String = m40007
  }

  final object APIKeyIsInValid extends BadRequest {
    @ApiModelProperty(example = c40008)  override val code: Int =       c40008.toInt
    @ApiModelProperty(example = m40008)  override val message: String = m40008
  }

  final object UserAlreadyExist extends BadRequest {
    @ApiModelProperty(example = c40009)  override val code: Int =       c40009.toInt
    @ApiModelProperty(example = m40009)  override val message: String = m40009
  }

  final object InvalidUserIdError extends BadRequest {
    @ApiModelProperty(example = c40010)  override val code: Int =       c40010.toInt
    @ApiModelProperty(example = m40010)  override val message: String = m40010
  }


  final object UserNameOrPasswordNotMatched extends BadRequest {
    @ApiModelProperty(example = c40012)  override val code: Int =       c40012.toInt
    @ApiModelProperty(example = m40012)  override val message: String = m40012
  }

  final object FileSizeLimitExceededError extends BadRequest {
    @ApiModelProperty(example = c40013)  override val code: Int =       c40013.toInt
    @ApiModelProperty(example = m40013)  override val message: String = m40013
  }

  final object UploadFileNotFound extends BadRequest {
    @ApiModelProperty(example = c40014)  override val code: Int =       c40014.toInt
    @ApiModelProperty(example = m40014)  override val message: String = m40014
  }

  final object NotAcceptableMimeTypeFound extends BadRequest {
    @ApiModelProperty(example = c40015)  override val code: Int =       c40015.toInt
    @ApiModelProperty(example = m40015)  override val message: String = m40015
  }

  final object UserAlreadyFollowed extends BadRequest {
    @ApiModelProperty(example = c40020)  override val code: Int =       c40020.toInt
    @ApiModelProperty(example = m40020)  override val message: String = m40020
  }

  final object UserNotFollowed extends BadRequest {
    @ApiModelProperty(example = c40021)  override val code: Int =       c40021.toInt
    @ApiModelProperty(example = m40021)  override val message: String = m40021
  }

  final object UserAlreadyBlocked extends BadRequest {
    @ApiModelProperty(example = c40022)  override val code: Int =       c40022.toInt
    @ApiModelProperty(example = m40022)  override val message: String = m40022
  }

  final object UserNotBlocked extends BadRequest {
    @ApiModelProperty(example = c40023)  override val code: Int =       c40023.toInt
    @ApiModelProperty(example = m40023)  override val message: String = m40023
  }

  final object UserAlreadyMuted extends BadRequest {
    @ApiModelProperty(example = c40024)  override val code: Int =       c40024.toInt
    @ApiModelProperty(example = m40024)  override val message: String = m40024
  }

  final object UserNotMuted extends BadRequest {
    @ApiModelProperty(example = c40025)  override val code: Int =       c40025.toInt
    @ApiModelProperty(example = m40025)  override val message: String = m40025
  }

  final object UserAlreadyFriend extends BadRequest {
    @ApiModelProperty(example = c40026)  override val code: Int =       c40026.toInt
    @ApiModelProperty(example = m40026)  override val message: String = m40026
  }

  final object UserNotFriend extends BadRequest {
    @ApiModelProperty(example = c40027)  override val code: Int =       c40027.toInt
    @ApiModelProperty(example = m40027)  override val message: String = m40027
  }

  final object UserAlreadyInvited extends BadRequest {
    @ApiModelProperty(example = c40028)  override val code: Int =       c40028.toInt
    @ApiModelProperty(example = m40028)  override val message: String = m40028
  }

  final object UserAlreadyRequested extends BadRequest {
    @ApiModelProperty(example = c40029)  override val code: Int =       c40029.toInt
    @ApiModelProperty(example = m40029)  override val message: String = m40029
  }

  final object UserAlreadyJoined extends BadRequest {
    @ApiModelProperty(example = c40030)  override val code: Int =       c40030.toInt
    @ApiModelProperty(example = m40030)  override val message: String = m40030
  }

  final object UserNotJoined extends BadRequest {
    @ApiModelProperty(example = c40031)  override val code: Int =       c40031.toInt
    @ApiModelProperty(example = m40031)  override val message: String = m40031
  }

  final object CommentAlreadyLiked extends BadRequest {
    @ApiModelProperty(example = c40032)  override val code: Int =       c40032.toInt
    @ApiModelProperty(example = m40032)  override val message: String = m40032
  }

  final object CommentNotLiked extends BadRequest {
    @ApiModelProperty(example = c40033)  override val code: Int =       c40033.toInt
    @ApiModelProperty(example = m40033)  override val message: String = m40033
  }

  final object TweetAlreadyLiked extends BadRequest {
    @ApiModelProperty(example = c40034)  override val code: Int =       c40034.toInt
    @ApiModelProperty(example = m40034)  override val message: String = m40034
  }

  final object TweetNotLiked extends BadRequest {
    @ApiModelProperty(example = c40035)  override val code: Int =       c40035.toInt
    @ApiModelProperty(example = m40035)  override val message: String = m40035
  }

  final object ChannelAlreadyHidden extends BadRequest {
    @ApiModelProperty(example = c40036)  override val code: Int =       c40036.toInt
    @ApiModelProperty(example = m40036)  override val message: String = m40036
  }

  final object ChannelNotHidden extends BadRequest {
    @ApiModelProperty(example = c40037)  override val code: Int =       c40037.toInt
    @ApiModelProperty(example = m40037)  override val message: String = m40037
  }

  final object TokenAlreadyExpired extends BadRequest {
    @ApiModelProperty(example = c40038)  override val code: Int =       c40038.toInt
    @ApiModelProperty(example = m40038)  override val message: String = m40038
  }

  final object InvalidTokenError extends BadRequest {
    @ApiModelProperty(example = c40039)  override val code: Int =       c40039.toInt
    @ApiModelProperty(example = m40039)  override val message: String = m40039
  }

  final object AccountAlreadyLinked extends BadRequest {
    @ApiModelProperty(example = c40040)  override val code: Int =       c40040.toInt
    @ApiModelProperty(example = m40040)  override val message: String = m40040
  }

  final object AccountNotLinked extends BadRequest {
    @ApiModelProperty(example = c40041)  override val code: Int =       c40041.toInt
    @ApiModelProperty(example = m40041)  override val message: String = m40041
  }

  final val c40400 = "40400"
  final val c40401 = "40401"
  final val c40402 = "40402"
  final val c40403 = "40403"
  final val c40404 = "40404"
  final val c40405 = "40405"
  final val c40406 = "40406"
  final val c40407 = "40407"
  final val c40408 = "40408"

  final val c40100 = "40100"
  final val c40101 = "40101"

  final val m40400 = "User not found."
  final val m40401 = "Comment not found."
  final val m40402 = "Medium not found."
  final val m40403 = "Tweet not found."
  final val m40404 = "Request not found."
  final val m40405 = "Channel not found."
  final val m40406 = "Invitation not found."
  final val m40407 = "Message not found."
  final val m40408 = "User not registered."

  final val m40100 = "Session not authorized."
  final val m40101 = "Access token expired."




  // 404 Not Found

  final object UserNotFound extends NotFound {
    @ApiModelProperty(example = c40400)  override val code: Int =       c40400.toInt
    @ApiModelProperty(example = m40400)  override val message: String = m40400
  }

  final object CommentNotFound extends NotFound {
    @ApiModelProperty(example = c40401)  override val code: Int =       c40401.toInt
    @ApiModelProperty(example = m40401)  override val message: String = m40401
  }

  final object MediumNotFound extends NotFound {
    @ApiModelProperty(example = c40402)  override val code: Int =       c40402.toInt
    @ApiModelProperty(example = m40402)  override val message: String = m40402
  }

  final object TweetNotFound extends NotFound {
    @ApiModelProperty(example = c40403)  override val code: Int =       c40403.toInt
    @ApiModelProperty(example = m40403)  override val message: String = m40403
  }

  final object FriendRequestNotFound extends NotFound {
    @ApiModelProperty(example = c40404)  override val code: Int =       c40404.toInt
    @ApiModelProperty(example = m40404)  override val message: String = m40404
  }

  final object ChannelNotFound extends NotFound {
    @ApiModelProperty(example = c40405)  override val code: Int =       c40405.toInt
    @ApiModelProperty(example = m40405)  override val message: String = m40405
  }

  final object InvitationNotFound extends NotFound {
    @ApiModelProperty(example = c40406)  override val code: Int =       c40406.toInt
    @ApiModelProperty(example = m40406)  override val message: String = m40406
  }

  final object MessageNotFound extends NotFound {
    @ApiModelProperty(example = c40407)  override val code: Int =       c40407.toInt
    @ApiModelProperty(example = m40407)  override val message: String = m40407
  }

  final object UserNotRegistered extends NotFound {
    @ApiModelProperty(example = c40408)  override val code: Int =       c40408.toInt
    @ApiModelProperty(example = m40408)  override val message: String = m40408
  }



  // 401 Unauthorized

  final object SessionNotAuthorized extends Unauthorized {
    @ApiModelProperty(example = c40100)  override val code: Int =       c40100.toInt
    @ApiModelProperty(example = m40100)  override val message: String = m40100
  }

  final object SessionTimeout extends Unauthorized {
    @ApiModelProperty(example = c40101)  override val code: Int =       c40101.toInt
    @ApiModelProperty(example = m40101)  override val message: String = m40101
  }


  // 400 Bad Request

  def invalidCountryCodesValidationError(message: String): ValidationErrorRequest     = { ValidationErrorRequest(40080, message) }
  def invalidTimeGranularityValidationError(message: String): ValidationErrorRequest  = { ValidationErrorRequest(40081, message) }
  def invalidUUIDValidationError(message: String): ValidationErrorRequest             = { ValidationErrorRequest(40082, message) }
  def invalidValuesValidationError(message: String): ValidationErrorRequest           = { ValidationErrorRequest(40083, message) }
  def jsonProcessingError(message: String): ValidationErrorRequest                    = { ValidationErrorRequest(40084, message) }
  def requiredFieldMissingValidationError(message: String): ValidationErrorRequest    = { ValidationErrorRequest(40085, message) }
  def sizeOutOfRangeValidation(message: String): ValidationErrorRequest               = { ValidationErrorRequest(40086, message) }
  def timeNotFutureValidation(message: String): ValidationErrorRequest                = { ValidationErrorRequest(40087, message) }
  def timeNotPastValidation(message: String): ValidationErrorRequest                  = { ValidationErrorRequest(40088, message) }
  def valueCannotBeEmptyValidation(message: String): ValidationErrorRequest           = { ValidationErrorRequest(40089, message) }
  def valueOutOfRangeValidation(message: String): ValidationErrorRequest              = { ValidationErrorRequest(40090, message) }
  def valueTooLargeValidation(message: String): ValidationErrorRequest                = { ValidationErrorRequest(40091, message) }
  def valueTooSmallValidation(message: String): ValidationErrorRequest                = { ValidationErrorRequest(40092, message) }
  def unknown(message: String): ValidationErrorRequest                                = { ValidationErrorRequest(40093, message) }

}