package io.github.cactacea.core.util.responses

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.twitter.finagle.http.Status

@JsonIgnoreProperties(Array("status"))
trait CactaceaError {
  val code: Long
  val message: String
  def status: Status
}

object CactaceaError {

  // 404 Not Found

  final object AccountNotFound                  extends NotFound(40400, "Account not found.")
  final object CommentNotFound                  extends NotFound(40401, "Comment not found.")
  final object MediumNotFound                   extends NotFound(40402, "Medium not found.")
  final object FeedNotFound                     extends NotFound(40403, "Feed not found.")
  final object FriendRequestNotFound            extends NotFound(40404, "Request not found.")
  final object GroupNotFound                    extends NotFound(40405, "Group not found.")
  final object GroupInviteNotFound              extends NotFound(40406, "Group invite not found.")

  // 400 Bad Request

  final object AccountAlreadyFollowed           extends BadRequest(40000, "Account already followed.")
  final object AccountNotFollowed               extends BadRequest(40001, "Account not followed.")
  final object AccountAlreadyBlocked            extends BadRequest(40002, "Account already blocked.")
  final object AccountNotBlocked                extends BadRequest(40003, "Account not blocked.")
  final object AccountAlreadyMuted              extends BadRequest(40004, "Account already muted.")
  final object AccountNotMuted                  extends BadRequest(40005, "Account not muted.")
  final object AccountAlreadyFriend             extends BadRequest(40006, "Account already friend.")
  final object AccountNotFriend                 extends BadRequest(40007, "Account not friend.")
  final object AccountAlreadyInvited            extends BadRequest(40008, "Account already invited.")
  final object AccountAlreadyRequested          extends BadRequest(40009, "Account already requested.")
  final object AccountAlreadyJoined             extends BadRequest(40010, "Account already joined.")
  final object AccountNotJoined                 extends BadRequest(40011, "Account not joined.")
  final object AccountNotSignedUp               extends BadRequest(40012, "Account not signed up.")
  final object AccountDeleted                   extends BadRequest(40013, "Account deleted.")
  final object AccountTerminated                extends BadRequest(40014, "Account terminated.")
  final object OperationNotAllowed              extends BadRequest(40015, "Operation not allowed.")
  final object CommentAlreadyFavorited          extends BadRequest(40016, "Comment already favorited.")
  final object CommentNotFavorited              extends BadRequest(40017, "Comment not favorited.")
  final object FeedAlreadyFavorited             extends BadRequest(40018, "Feed already favorited.")
  final object FeedNotFavorited                 extends BadRequest(40019, "Feed not favorited.")
  final object SocialAccountAlreadyConnected    extends BadRequest(40020, "Social Account already connected.")
  final object SocialAccountNotConnected        extends BadRequest(40021, "Social Account not connected")
  final object FileUploadErrorOccurred          extends BadRequest(40022, "File upload error occurred.")
  final object AuthorityNotFound                extends BadRequest(40023, "Authority not found.")
  final object GroupIsInvitationOnly            extends BadRequest(40024, "Group is invitation only.")
  final object DirectMessageGroupCanNotUpdated  extends BadRequest(40025, "Direct message group can not updated.")
  final object APIKeyIsInValid                  extends BadRequest(40026, "API Key is invalid.")
  final object AccountNameAlreadyUsed           extends BadRequest(40027, "Account name already used.")
  final object CanNotSpecifyMyself              extends BadRequest(40028, "Can not specify myself.")
  final object PasswordNotMatched               extends BadRequest(40029, "Password not matched.")
  final object InvalidAccountNameOrPassword     extends BadRequest(40030, "Invalid Account name or password.")

  final object SessionNotAuthorized             extends Unauthorized(40100, "Session not authorized.")
  final object SessionTimeout                   extends Unauthorized(40101, "Access token expired.")

  // 406 Not Acceptable
  final object NotAcceptableMimeTypeFound       extends NotFound(40400, "Not acceptable mime type found.")

  def InvalidCountryCodesValidationError(message: String)     = { BadRequest(40080, message) }
  def InvalidTimeGranularityValidationError(message: String)  = { BadRequest(40081, message) }
  def InvalidUUIDValidationError(message: String)             = { BadRequest(40082, message) }
  def InvalidValuesValidationError(message: String)           = { BadRequest(40083, message) }
  def JsonProcessingError(message: String)                    = { BadRequest(40084, message) }
  def RequiredFieldMissingValidationError(message: String)    = { BadRequest(40085, message) }
  def SizeOutOfRangeValidation(message: String)               = { BadRequest(40086, message) }
  def TimeNotFutureValidation(message: String)                = { BadRequest(40087, message) }
  def TimeNotPastValidation(message: String)                  = { BadRequest(40088, message) }
  def ValueCannotBeEmptyValidation(message: String)           = { BadRequest(40089, message) }
  def ValueOutOfRangeValidation(message: String)              = { BadRequest(40090, message) }
  def ValueTooLargeValidation(message: String)                = { BadRequest(40091, message) }
  def ValueTooSmallValidation(message: String)                = { BadRequest(40092, message) }

  def Unknown(message: String)                                = { BadRequest(40093, message) }

}
