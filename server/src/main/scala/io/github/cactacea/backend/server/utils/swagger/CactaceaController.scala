package io.github.cactacea.backend.server.utils.swagger

import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator
import io.github.cactacea.finagger.SwaggerController

trait CactaceaController extends SwaggerController {
  self: Controller =>

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."

  protected val usersTag = "Users"
  protected val blocksTag = "Blocks"
  protected val commentsTag = "Comments"
  protected val commentLikesTag = "CommentLikes"
  protected val tweetsTag = "Tweets"
  protected val tweetsLikeTag = "TweetLikes"
  protected val channelsTag = "Channels"
  protected val invitationsTag = "Invitations"
  protected val sessionTag = "Session"
  protected val mediumsTag = "Mediums"
  protected val notificationsTag = "Notifications"
  protected val friendRequestsTag = "FriendRequests"
  protected val sessionsTag = "Sessions"
  protected val settingsTag = "Settings"


  def scope(auth: Authorization[Authentication, JWTAuthenticator])(implicit factory: CactaceaAuthenticationFilterFactory) = {
    filter(factory.create(auth))
  }
}
