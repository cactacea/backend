package io.github.cactacea.backend.core.util.responses

import io.swagger.annotations.ApiModelProperty

object CactaceaErrors {

  final val c20000 = "20000"
  final val c20001 = "20001"
  final val c20002 = "20002"
  final val c20003 = "20003"
  final val c20004 = "20004"
  final val c20005 = "20005"
  final val c20006 = "20006"
  final val c20007 = "20007"
  final val c20008 = "20008"
  final val c20009 = "20009"
  final val c20010 = "20010"
  final val c20011 = "20011"
  final val c20012 = "20012"
  final val c20013 = "20013"
  final val c20014 = "20014"
  final val c20015 = "20015"
  final val c20016 = "20016"
  final val c20017 = "20017"
  final val c20018 = "20018"
  final val c20019 = "20019"

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
  final val c40011 = "40011"
  final val c40012 = "40012"
  final val c40013 = "40013"
  final val c40014 = "40014"

  final val c40400 = "40400"
  final val c40401 = "40401"
  final val c40402 = "40402"
  final val c40403 = "40403"
  final val c40404 = "40404"
  final val c40405 = "40405"
  final val c40406 = "40406"
  final val c40407 = "40407"

  final val c40600 = "40600"
  final val c40601 = "40601"

  final val m20000 = "Account already follows."
  final val m20001 = "Account not follows."
  final val m20002 = "Account already blocked."
  final val m20003 = "Account not blocked."
  final val m20004 = "Account already muted."
  final val m20005 = "Account not muted."
  final val m20006 = "Account already friend."
  final val m20007 = "Account not friend."
  final val m20008 = "Account already invitationd."
  final val m20009 = "Account already requested."
  final val m20010 = "Account already joined."
  final val m20011 = "Account not joined."
  final val m20012 = "Comment already liked."
  final val m20013 = "Comment not liked."
  final val m20014 = "Feed already liked."
  final val m20015 = "Feed not liked."
  final val m20016 = "Social Account already connected."
  final val m20017 = "Social Account not connected"
  final val m20018 = "Group already hidden."
  final val m20019 = "Group not hidden."

  final val m40000 = "Account not signed up."
  final val m40001 = "Account deleted."
  final val m40002 = "Account terminated."
  final val m40003 = "Operation not allowed."
  final val m40004 = "File upload error occurred."
  final val m40005 = "Authority not found."
  final val m40006 = "Group is invitation only."
  final val m40007 = "Direct message group can not updated."
  final val m40008 = "API Key is invalid."
  final val m40009 = "Account name already used."
  final val m40010 = "Can not specify myself."
  final val m40011 = "Password not matched."
  final val m40012 = "Invalid Account name or password."
  final val m40013 = "Maximum number of group accounts reached."
  final val m40014 = "Not acceptable mime type found."

  final val m40600 = "Session not authorized."
  final val m40601 = "Access token expired."

  final val m40400 = "Account not found."
  final val m40401 = "Comment not found."
  final val m40402 = "Medium not found."
  final val m40403 = "Feed not found."
  final val m40404 = "Request not found."
  final val m40405 = "Group not found."
  final val m40406 = "Group invitation not found."
  final val m40407 = "Social account not found."

  // 200 Ok

  final object AccountAlreadyFollowed extends Ok {
    @ApiModelProperty(example = c20000)  override val code: Int =       c20000.toInt
    @ApiModelProperty(example = m20000)  override val message: String = m20000
  }

  final object AccountNotFollowed extends Ok {
    @ApiModelProperty(example = c20001)  override val code: Int =       c20001.toInt
    @ApiModelProperty(example = m20001)  override val message: String = m20001
  }

  final object AccountAlreadyBlocked extends Ok {
    @ApiModelProperty(example = c20002)  override val code: Int =       c20002.toInt
    @ApiModelProperty(example = m20002)  override val message: String = m20002
  }

  final object AccountNotBlocked extends Ok {
    @ApiModelProperty(example = c20003)  override val code: Int =       c20003.toInt
    @ApiModelProperty(example = m20003)  override val message: String = m20003
  }

  final object AccountAlreadyMuted extends Ok {
    @ApiModelProperty(example = c20004)  override val code: Int =       c20004.toInt
    @ApiModelProperty(example = m20004)  override val message: String = m20004
  }

  final object AccountNotMuted extends Ok {
    @ApiModelProperty(example = c20005)  override val code: Int =       c20005.toInt
    @ApiModelProperty(example = m20005)  override val message: String = m20005
  }

  final object AccountAlreadyFriend extends Ok {
    @ApiModelProperty(example = c20006)  override val code: Int =       c20006.toInt
    @ApiModelProperty(example = m20006)  override val message: String = m20006
  }

  final object AccountNotFriend extends Ok {
    @ApiModelProperty(example = c20007)  override val code: Int =       c20007.toInt
    @ApiModelProperty(example = m20007)  override val message: String = m20007
  }

  final object AccountAlreadyInvited extends Ok {
    @ApiModelProperty(example = c20008)  override val code: Int =       c20008.toInt
    @ApiModelProperty(example = m20008)  override val message: String = m20008
  }

  final object AccountAlreadyRequested extends Ok {
    @ApiModelProperty(example = c20009)  override val code: Int =       c20009.toInt
    @ApiModelProperty(example = m20009)  override val message: String = m20009
  }

  final object AccountAlreadyJoined extends Ok {
    @ApiModelProperty(example = c20010)  override val code: Int =       c20010.toInt
    @ApiModelProperty(example = m20010)  override val message: String = m20010
  }

  final object AccountNotJoined extends Ok {
    @ApiModelProperty(example = c20011)  override val code: Int =       c20011.toInt
    @ApiModelProperty(example = m20011)  override val message: String = m20011
  }

  final object CommentAlreadyLiked extends Ok {
    @ApiModelProperty(example = c20012)  override val code: Int =       c20012.toInt
    @ApiModelProperty(example = m20012)  override val message: String = m20012
  }

  final object CommentNotLiked extends Ok {
    @ApiModelProperty(example = c20013)  override val code: Int =       c20013.toInt
    @ApiModelProperty(example = m20013)  override val message: String = m20013
  }

  final object FeedAlreadyLiked extends Ok {
    @ApiModelProperty(example = c20014)  override val code: Int =       c20014.toInt
    @ApiModelProperty(example = m20014)  override val message: String = m20014
  }

  final object FeedNotLiked extends Ok {
    @ApiModelProperty(example = c20015)  override val code: Int =       c20015.toInt
    @ApiModelProperty(example = m20015)  override val message: String = m20015
  }

  final object SocialAccountAlreadyConnected extends Ok {
    @ApiModelProperty(example = c20016)  override val code: Int =       c20016.toInt
    @ApiModelProperty(example = m20016)  override val message: String = m20016
  }

  final object SocialAccountNotConnected extends Ok {
    @ApiModelProperty(example = c20017)  override val code: Int =       c20017.toInt
    @ApiModelProperty(example = m20017)  override val message: String = m20017
  }

  final object GroupAlreadyHidden extends Ok {
    @ApiModelProperty(example = c20018)  override val code: Int =       c20018.toInt
    @ApiModelProperty(example = m20018)  override val message: String = m20018
  }

  final object GroupNotHidden extends Ok {
    @ApiModelProperty(example = c20019)  override val code: Int =       c20019.toInt
    @ApiModelProperty(example = m20019)  override val message: String = m20019
  }




  // 400 Bad Request

  final object AccountNotSignedUp extends BadRequest {
    @ApiModelProperty(example = c40000)  override val code: Int =       c40000.toInt
    @ApiModelProperty(example = m40000)  override val message: String = m40000
  }

  final object AccountDeleted extends BadRequest {
    @ApiModelProperty(example = c40001)  override val code: Int =       c40001.toInt
    @ApiModelProperty(example = m40001)  override val message: String = m40001
  }

  final object AccountTerminated extends BadRequest {
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

  final object GroupIsInvitationOnly extends BadRequest {
    @ApiModelProperty(example = c40006)  override val code: Int =       c40006.toInt
    @ApiModelProperty(example = m40006)  override val message: String = m40006
  }

  final object DirectMessageGroupCanNotUpdated extends BadRequest {
    @ApiModelProperty(example = c40007)  override val code: Int =       c40007.toInt
    @ApiModelProperty(example = m40007)  override val message: String = m40007
  }

  final object APIKeyIsInValid extends BadRequest {
    @ApiModelProperty(example = c40008)  override val code: Int =       c40008.toInt
    @ApiModelProperty(example = m40008)  override val message: String = m40008
  }

  final object AccountNameAlreadyUsed extends BadRequest {
    @ApiModelProperty(example = c40009)  override val code: Int =       c40009.toInt
    @ApiModelProperty(example = m40009)  override val message: String = m40009
  }

  final object CanNotSpecifyMyself extends BadRequest {
    @ApiModelProperty(example = c40010)  override val code: Int =       c40010.toInt
    @ApiModelProperty(example = m40010)  override val message: String = m40010
  }

  final object PasswordNotMatched extends BadRequest {
    @ApiModelProperty(example = c40011)  override val code: Int =       c40011.toInt
    @ApiModelProperty(example = m40011)  override val message: String = m40011
  }

  final object InvalidAccountNameOrPassword extends BadRequest {
    @ApiModelProperty(example = c40012)  override val code: Int =       c40012.toInt
    @ApiModelProperty(example = m40012)  override val message: String = m40012
  }

  final object GroupAccountsCountLimitError extends BadRequest {
    @ApiModelProperty(example = c40013)  override val code: Int =       c40013.toInt
    @ApiModelProperty(example = m40013)  override val message: String = m40013
  }

  final object NotAcceptableMimeTypeFound extends BadRequest {
    @ApiModelProperty(example = c40014)  override val code: Int =       c40014.toInt
    @ApiModelProperty(example = m40014)  override val message: String = m40014
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
