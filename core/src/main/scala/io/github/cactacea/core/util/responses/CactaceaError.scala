package io.github.cactacea.core.util.responses

import com.twitter.finagle.http.Status
import io.swagger.annotations.ApiModelProperty

trait CactaceaError {
  def status: Status
}

object CactaceaError {

  type AccountNotFoundType = AccountNotFound.type
  type CommentNotFoundType = CommentNotFound.type
  type MediumNotFoundType = MediumNotFound.type
  type FeedNotFoundType = FeedNotFound.type
  type FriendRequestNotFoundType = FriendRequestNotFound.type
  type GroupNotFoundType = GroupNotFound.type
  type GroupInvitationNotFoundType = GroupInvitationNotFound.type
  type SocialAccountNotFoundType = SocialAccountNotFound.type
  type AccountAlreadyFollowedType = AccountAlreadyFollowed.type
  type AccountNotFollowedType = AccountNotFollowed.type
  type AccountAlreadyBlockedType = AccountAlreadyBlocked.type
  type AccountNotBlockedType = AccountNotBlocked.type
  type AccountAlreadyMutedType = AccountAlreadyMuted.type
  type AccountNotMutedType = AccountNotMuted.type
  type AccountAlreadyFriendType = AccountAlreadyFriend.type
  type AccountNotFriendType = AccountNotFriend.type
  type AccountAlreadyInvitedType = AccountAlreadyInvited.type
  type AccountAlreadyRequestedType = AccountAlreadyRequested.type
  type AccountAlreadyJoinedType = AccountAlreadyJoined.type
  type AccountNotJoinedType = AccountNotJoined.type
  type AccountNotSignedUpType = AccountNotSignedUp.type
  type AccountDeletedType = AccountDeleted.type
  type AccountTerminatedType = AccountTerminated.type
  type OperationNotAllowedType = OperationNotAllowed.type
  type CommentAlreadyLikedType = CommentAlreadyLiked.type
  type CommentNotLikedType = CommentNotLiked.type
  type FeedAlreadyLikedType = FeedAlreadyLiked.type
  type FeedNotLikedType = FeedNotLiked.type
  type SocialAccountAlreadyConnectedType = SocialAccountAlreadyConnected.type
  type SocialAccountNotConnectedType = SocialAccountNotConnected.type
  type FileUploadErrorOccurredType = FileUploadErrorOccurred.type
  type AuthorityNotFoundType = AuthorityNotFound.type
  type GroupIsInvitationOnlyType = GroupIsInvitationOnly.type
  type DirectMessageGroupCanNotUpdatedType = DirectMessageGroupCanNotUpdated.type
  type APIKeyIsInValidType = APIKeyIsInValid.type
  type AccountNameAlreadyUsedType = AccountNameAlreadyUsed.type
  type CanNotSpecifyMyselfType = CanNotSpecifyMyself.type
  type PasswordNotMatchedType = PasswordNotMatched.type
  type InvalidAccountNameOrPasswordType = InvalidAccountNameOrPassword.type
  type GroupAccountsCountLimitErrorType = GroupAccountsCountLimitError.type
  type GroupAlreadyHiddenType = GroupAlreadyHidden.type
  type GroupNotHiddenType = GroupNotHidden.type
  type SessionNotAuthorizedType = SessionNotAuthorized.type
  type SessionTimeoutType = SessionTimeout.type
  type ValidationErrorType = ValidationError.type


  import CactaceaErrorMessages._

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

  // 400 Bad Request

  final object AccountAlreadyFollowed extends BadRequest {
    @ApiModelProperty(example = c40000)  override val code: Int =       c40000.toInt
    @ApiModelProperty(example = m40000)  override val message: String = m40000
  }

  final object AccountNotFollowed extends BadRequest {
    @ApiModelProperty(example = c40001)  override val code: Int =       c40001.toInt
    @ApiModelProperty(example = m40001)  override val message: String = m40001
  }

  final object AccountAlreadyBlocked extends BadRequest {
    @ApiModelProperty(example = c40002)  override val code: Int =       c40002.toInt
    @ApiModelProperty(example = m40002)  override val message: String = m40002
  }

  final object AccountNotBlocked extends BadRequest {
    @ApiModelProperty(example = c40003)  override val code: Int =       c40003.toInt
    @ApiModelProperty(example = m40003)  override val message: String = m40003
  }

  final object AccountAlreadyMuted extends BadRequest {
    @ApiModelProperty(example = c40004)  override val code: Int =       c40004.toInt
    @ApiModelProperty(example = m40004)  override val message: String = m40004
  }

  final object AccountNotMuted extends BadRequest {
    @ApiModelProperty(example = c40005)  override val code: Int =       c40005.toInt
    @ApiModelProperty(example = m40005)  override val message: String = m40005
  }

  final object AccountAlreadyFriend extends BadRequest {
    @ApiModelProperty(example = c40006)  override val code: Int =       c40006.toInt
    @ApiModelProperty(example = m40006)  override val message: String = m40006
  }

  final object AccountNotFriend extends BadRequest {
    @ApiModelProperty(example = c40007)  override val code: Int =       c40007.toInt
    @ApiModelProperty(example = m40007)  override val message: String = m40007
  }

  final object AccountAlreadyInvited extends BadRequest {
    @ApiModelProperty(example = c40008)  override val code: Int =       c40008.toInt
    @ApiModelProperty(example = m40008)  override val message: String = m40008
  }

  final object AccountAlreadyRequested extends BadRequest {
    @ApiModelProperty(example = c40009)  override val code: Int =       c40009.toInt
    @ApiModelProperty(example = m40009)  override val message: String = m40009
  }

  final object AccountAlreadyJoined extends BadRequest {
    @ApiModelProperty(example = c40010)  override val code: Int =       c40010.toInt
    @ApiModelProperty(example = m40010)  override val message: String = m40010
  }

  final object AccountNotJoined extends BadRequest {
    @ApiModelProperty(example = c40011)  override val code: Int =       c40011.toInt
    @ApiModelProperty(example = m40011)  override val message: String = m40011
  }

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

  final object CommentAlreadyLiked extends BadRequest {
    @ApiModelProperty(example = c40016)  override val code: Int =       c40016.toInt
    @ApiModelProperty(example = m40016)  override val message: String = m40016
  }

  final object CommentNotLiked extends BadRequest {
    @ApiModelProperty(example = c40017)  override val code: Int =       c40017.toInt
    @ApiModelProperty(example = m40017)  override val message: String = m40017
  }

  final object FeedAlreadyLiked extends BadRequest {
    @ApiModelProperty(example = c40018)  override val code: Int =       c40018.toInt
    @ApiModelProperty(example = m40018)  override val message: String = m40018
  }

  final object FeedNotLiked extends BadRequest {
    @ApiModelProperty(example = c40019)  override val code: Int =       c40019.toInt
    @ApiModelProperty(example = m40019)  override val message: String = m40019
  }

  final object SocialAccountAlreadyConnected extends BadRequest {
    @ApiModelProperty(example = c40020)  override val code: Int =       c40020.toInt
    @ApiModelProperty(example = m40020)  override val message: String = m40020
  }

  final object SocialAccountNotConnected extends BadRequest {
    @ApiModelProperty(example = c40021)  override val code: Int =       c40021.toInt
    @ApiModelProperty(example = m40021)  override val message: String = m40021
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

  final object GroupAlreadyHidden extends BadRequest {
    @ApiModelProperty(example = c40032)  override val code: Int =       c40032.toInt
    @ApiModelProperty(example = m40032)  override val message: String = m40032
  }

  final object GroupNotHidden extends BadRequest {
    @ApiModelProperty(example = c40033)  override val code: Int =       c40033.toInt
    @ApiModelProperty(example = m40033)  override val message: String = m40033
  }

  final object NotAcceptableMimeTypeFound extends BadRequest {
    @ApiModelProperty(example = c40034)  override val code: Int =       c40034.toInt
    @ApiModelProperty(example = m40034)  override val message: String = m40034
  }

  // 406 Unauthorized

  final object SessionNotAuthorized extends BadRequest {
    @ApiModelProperty(example = c40100)  override val code: Int =       c40100.toInt
    @ApiModelProperty(example = m40100)  override val message: String = m40100
  }

  final object SessionTimeout extends BadRequest {
    @ApiModelProperty(example = c40101)  override val code: Int =       c40101.toInt
    @ApiModelProperty(example = m40101)  override val message: String = m40101
  }

  // 400 Bad Request

  object ValidationError extends BadRequest {
    @ApiModelProperty(example = "400XX")  override val code: Int =       c40080.toInt
    @ApiModelProperty(example = m40080)  override val message: String = m40080
  }

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
