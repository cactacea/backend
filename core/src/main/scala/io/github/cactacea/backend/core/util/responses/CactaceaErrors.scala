package io.github.cactacea.backend.core.util.responses

import io.swagger.annotations.ApiModelProperty

object CactaceaErrors {

  final val c40400 = "40400"
  final val c40401 = "40401"
  final val c40402 = "40402"
  final val c40403 = "40403"
  final val c40404 = "40404"
  final val c40405 = "40405"
  final val c40406 = "40406"
  final val c40407 = "40407"
  final val c30400 = "40000"
  final val c30401 = "40001"
  final val c30402 = "40002"
  final val c30403 = "40003"
  final val c30404 = "40004"
  final val c30405 = "40005"
  final val c30406 = "40006"
  final val c30407 = "40007"
  final val c30408 = "40008"
  final val c30409 = "40009"
  final val c30410 = "40010"
  final val c30411 = "40011"
  final val c40012 = "40012"
  final val c40013 = "40013"
  final val c40014 = "40014"
  final val c40015 = "40015"
  final val c30416 = "40016"
  final val c30417 = "40017"
  final val c30418 = "40018"
  final val c30419 = "40019"
  final val c30420 = "40020"
  final val c30421 = "40021"
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
  final val c30432 = "40032"
  final val c30433 = "40033"
  final val c40034 = "40034"
  final val c40600 = "40100"
  final val c40601 = "40101"
  final val c40080 = "40080"

  final val m40400 = "Account not found."
  final val m40401 = "Comment not found."
  final val m40402 = "Medium not found."
  final val m40403 = "Feed not found."
  final val m40404 = "Request not found."
  final val m40405 = "Group not found."
  final val m40406 = "Group invitation not found."
  final val m40407 = "Social account not found."
  final val m30400 = "Account already follows."
  final val m30401 = "Account not follows."
  final val m30402 = "Account already blocked."
  final val m30403 = "Account not blocked."
  final val m30404 = "Account already muted."
  final val m30405 = "Account not muted."
  final val m30406 = "Account already friend."
  final val m30407 = "Account not friend."
  final val m30408 = "Account already invitationd."
  final val m30409 = "Account already requested."
  final val m30410 = "Account already joined."
  final val m30411 = "Account not joined."
  final val m40012 = "Account not signed up."
  final val m40013 = "Account deleted."
  final val m40014 = "Account terminated."
  final val m40015 = "Operation not allowed."
  final val m30416 = "Comment already liked."
  final val m30417 = "Comment not liked."
  final val m30418 = "Feed already liked."
  final val m30419 = "Feed not liked."
  final val m30420 = "Social Account already connected."
  final val m30421 = "Social Account not connected"
  final val m40022 = "File upload error occurred."
  final val m40023 = "Authority not found."
  final val m40024 = "Group is invitation only."
  final val m40025 = "Direct message group can not updated."
  final val m40026 = "API Key is invalid."
  final val m40027 = "Account name already used."
  final val m40028 = "Can not specify myself."
  final val m40029 = "Password not matched."
  final val m40030 = "Invalid Account name or password."
  final val m40031 = "Maximum number of group accounts reached."
  final val m30432 = "Group already hidden."
  final val m30433 = "Group not hidden."
  final val m40034 = "Not acceptable mime type found."
  final val m40600 = "Session not authorized."
  final val m40601 = "Access token expired."
  final val m40080 = "Validation error message is here."

  // 304 Not Modified

  final object AccountAlreadyFollowed extends NotModified {
    @ApiModelProperty(example = c30400)  override val code: Int =       c30400.toInt
    @ApiModelProperty(example = m30400)  override val message: String = m30400
  }

  final object AccountNotFollowed extends NotModified {
    @ApiModelProperty(example = c30401)  override val code: Int =       c30401.toInt
    @ApiModelProperty(example = m30401)  override val message: String = m30401
  }

  final object AccountAlreadyBlocked extends NotModified {
    @ApiModelProperty(example = c30402)  override val code: Int =       c30402.toInt
    @ApiModelProperty(example = m30402)  override val message: String = m30402
  }

  final object AccountNotBlocked extends NotModified {
    @ApiModelProperty(example = c30403)  override val code: Int =       c30403.toInt
    @ApiModelProperty(example = m30403)  override val message: String = m30403
  }

  final object AccountAlreadyMuted extends NotModified {
    @ApiModelProperty(example = c30404)  override val code: Int =       c30404.toInt
    @ApiModelProperty(example = m30404)  override val message: String = m30404
  }

  final object AccountNotMuted extends NotModified {
    @ApiModelProperty(example = c30405)  override val code: Int =       c30405.toInt
    @ApiModelProperty(example = m30405)  override val message: String = m30405
  }

  final object AccountAlreadyFriend extends NotModified {
    @ApiModelProperty(example = c30406)  override val code: Int =       c30406.toInt
    @ApiModelProperty(example = m30406)  override val message: String = m30406
  }

  final object AccountNotFriend extends NotModified {
    @ApiModelProperty(example = c30407)  override val code: Int =       c30407.toInt
    @ApiModelProperty(example = m30407)  override val message: String = m30407
  }

  final object AccountAlreadyInvited extends NotModified {
    @ApiModelProperty(example = c30408)  override val code: Int =       c30408.toInt
    @ApiModelProperty(example = m30408)  override val message: String = m30408
  }

  final object AccountAlreadyRequested extends NotModified {
    @ApiModelProperty(example = c30409)  override val code: Int =       c30409.toInt
    @ApiModelProperty(example = m30409)  override val message: String = m30409
  }

  final object AccountAlreadyJoined extends NotModified {
    @ApiModelProperty(example = c30410)  override val code: Int =       c30410.toInt
    @ApiModelProperty(example = m30410)  override val message: String = m30410
  }

  final object AccountNotJoined extends NotModified {
    @ApiModelProperty(example = c30411)  override val code: Int =       c30411.toInt
    @ApiModelProperty(example = m30411)  override val message: String = m30411
  }

  final object CommentAlreadyLiked extends NotModified {
    @ApiModelProperty(example = c30416)  override val code: Int =       c30416.toInt
    @ApiModelProperty(example = m30416)  override val message: String = m30416
  }

  final object CommentNotLiked extends NotModified {
    @ApiModelProperty(example = c30417)  override val code: Int =       c30417.toInt
    @ApiModelProperty(example = m30417)  override val message: String = m30417
  }

  final object FeedAlreadyLiked extends NotModified {
    @ApiModelProperty(example = c30418)  override val code: Int =       c30418.toInt
    @ApiModelProperty(example = m30418)  override val message: String = m30418
  }

  final object FeedNotLiked extends NotModified {
    @ApiModelProperty(example = c30419)  override val code: Int =       c30419.toInt
    @ApiModelProperty(example = m30419)  override val message: String = m30419
  }

  final object SocialAccountAlreadyConnected extends NotModified {
    @ApiModelProperty(example = c30420)  override val code: Int =       c30420.toInt
    @ApiModelProperty(example = m30420)  override val message: String = m30420
  }

  final object SocialAccountNotConnected extends NotModified {
    @ApiModelProperty(example = c30421)  override val code: Int =       c30421.toInt
    @ApiModelProperty(example = m30421)  override val message: String = m30421
  }

  final object GroupAlreadyHidden extends NotModified {
    @ApiModelProperty(example = c30432)  override val code: Int =       c30432.toInt
    @ApiModelProperty(example = m30432)  override val message: String = m30432
  }

  final object GroupNotHidden extends NotModified {
    @ApiModelProperty(example = c30433)  override val code: Int =       c30433.toInt
    @ApiModelProperty(example = m30433)  override val message: String = m30433
  }




  // 400 Bad Request

  final object AccountNotSignedUp extends BadRequest {
    @ApiModelProperty(example = c40012)  override val code: Int =       c40012.toInt
    @ApiModelProperty(example = m40012)  override val message: String = m40012
  }

  final object AccountDeleted extends BadRequest {
    @ApiModelProperty(example = c40013)  override val code: Int =       c40013.toInt
    @ApiModelProperty(example = m40013)  override val message: String = m40013
  }

  final object AccountTerminated extends BadRequest {
    @ApiModelProperty(example = c40014)  override val code: Int =       c40014.toInt
    @ApiModelProperty(example = m40014)  override val message: String = m40014
  }

  final object OperationNotAllowed extends BadRequest {
    @ApiModelProperty(example = c40015)  override val code: Int =       c40015.toInt
    @ApiModelProperty(example = m40015)  override val message: String = m40015
  }

  final object FileUploadErrorOccurred extends BadRequest {
    @ApiModelProperty(example = c40022)  override val code: Int =       c40022.toInt
    @ApiModelProperty(example = m40022)  override val message: String = m40022
  }

  final object AuthorityNotFound extends BadRequest {
    @ApiModelProperty(example = c40023)  override val code: Int =       c40023.toInt
    @ApiModelProperty(example = m40023)  override val message: String = m40023
  }

  final object GroupIsInvitationOnly extends BadRequest {
    @ApiModelProperty(example = c40024)  override val code: Int =       c40024.toInt
    @ApiModelProperty(example = m40024)  override val message: String = m40024
  }

  final object DirectMessageGroupCanNotUpdated extends BadRequest {
    @ApiModelProperty(example = c40025)  override val code: Int =       c40025.toInt
    @ApiModelProperty(example = m40025)  override val message: String = m40025
  }

  final object APIKeyIsInValid extends BadRequest {
    @ApiModelProperty(example = c40026)  override val code: Int =       c40026.toInt
    @ApiModelProperty(example = m40026)  override val message: String = m40026
  }

  final object AccountNameAlreadyUsed extends BadRequest {
    @ApiModelProperty(example = c40027)  override val code: Int =       c40027.toInt
    @ApiModelProperty(example = m40027)  override val message: String = m40027
  }

  final object CanNotSpecifyMyself extends BadRequest {
    @ApiModelProperty(example = c40028)  override val code: Int =       c40028.toInt
    @ApiModelProperty(example = m40028)  override val message: String = m40028
  }

  final object PasswordNotMatched extends BadRequest {
    @ApiModelProperty(example = c40029)  override val code: Int =       c40029.toInt
    @ApiModelProperty(example = m40029)  override val message: String = m40029
  }

  final object InvalidAccountNameOrPassword extends BadRequest {
    @ApiModelProperty(example = c40030)  override val code: Int =       c40030.toInt
    @ApiModelProperty(example = m40030)  override val message: String = m40030
  }

  final object GroupAccountsCountLimitError extends BadRequest {
    @ApiModelProperty(example = c40031)  override val code: Int =       c40031.toInt
    @ApiModelProperty(example = m40031)  override val message: String = m40031
  }

  final object NotAcceptableMimeTypeFound extends BadRequest {
    @ApiModelProperty(example = c40034)  override val code: Int =       c40034.toInt
    @ApiModelProperty(example = m40034)  override val message: String = m40034
  }


  // 404 Not Found

  final object AccountNotFound extends NotFound {
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

  final object FeedNotFound extends NotFound {
    @ApiModelProperty(example = c40403)  override val code: Int =       c40403.toInt
    @ApiModelProperty(example = m40403)  override val message: String = m40403
  }

  final object FriendRequestNotFound extends NotFound {
    @ApiModelProperty(example = c40404)  override val code: Int =       c40404.toInt
    @ApiModelProperty(example = m40404)  override val message: String = m40404
  }

  final object GroupNotFound extends NotFound {
    @ApiModelProperty(example = c40405)  override val code: Int =       c40405.toInt
    @ApiModelProperty(example = m40405)  override val message: String = m40405
  }

  final object GroupInvitationNotFound extends NotFound {
    @ApiModelProperty(example = c40406)  override val code: Int =       c40406.toInt
    @ApiModelProperty(example = m40406)  override val message: String = m40406
  }

  final object SocialAccountNotFound extends NotFound {
    @ApiModelProperty(example = c40407)  override val code: Int =       c40407.toInt
    @ApiModelProperty(example = m40407)  override val message: String = m40407
  }



  // 406 Unauthorized

  final object SessionNotAuthorized extends Unauthorized {
    @ApiModelProperty(example = c40600)  override val code: Int =       c40600.toInt
    @ApiModelProperty(example = m40600)  override val message: String = m40600
  }

  final object SessionTimeout extends Unauthorized {
    @ApiModelProperty(example = c40601)  override val code: Int =       c40601.toInt
    @ApiModelProperty(example = m40601)  override val message: String = m40601
  }

  // 400 Bad Request

  def InvalidCountryCodesValidationError(message: String)     = { ValidationErrorRequest(40080, message) }
  def InvalidTimeGranularityValidationError(message: String)  = { ValidationErrorRequest(40081, message) }
  def InvalidUUIDValidationError(message: String)             = { ValidationErrorRequest(40082, message) }
  def InvalidValuesValidationError(message: String)           = { ValidationErrorRequest(40083, message) }
  def JsonProcessingError(message: String)                    = { ValidationErrorRequest(40084, message) }
  def RequiredFieldMissingValidationError(message: String)    = { ValidationErrorRequest(40085, message) }
  def SizeOutOfRangeValidation(message: String)               = { ValidationErrorRequest(40086, message) }
  def TimeNotFutureValidation(message: String)                = { ValidationErrorRequest(40087, message) }
  def TimeNotPastValidation(message: String)                  = { ValidationErrorRequest(40088, message) }
  def ValueCannotBeEmptyValidation(message: String)           = { ValidationErrorRequest(40089, message) }
  def ValueOutOfRangeValidation(message: String)              = { ValidationErrorRequest(40090, message) }
  def ValueTooLargeValidation(message: String)                = { ValidationErrorRequest(40091, message) }
  def ValueTooSmallValidation(message: String)                = { ValidationErrorRequest(40092, message) }
  def Unknown(message: String)                                = { ValidationErrorRequest(40093, message) }

}
