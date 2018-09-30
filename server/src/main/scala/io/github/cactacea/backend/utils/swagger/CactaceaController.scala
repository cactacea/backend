package io.github.cactacea.backend.swagger

import com.twitter.finatra.http.Controller
import io.cactacea.finagger.SwaggerController
import io.github.cactacea.backend.utils.auth.PermissionController

trait CactaceaController extends SwaggerController with PermissionController {
  self: Controller =>

  protected val successfulMessage = "Successful operation."
  protected val validationErrorMessage = "Validation error occurred."

  protected val accountsTag = "Accounts"
  protected val blocksTag = "Blocks"
  protected val commentsTag = "Comments"
  protected val feedsTag = "Feeds"
  protected val followsTag = "Follows"
  protected val friendsTag = "Friends"
  protected val groupsTag = "Groups"
  protected val invitationsTag = "Invitations"
  protected val sessionTag = "Session"
  protected val mediumsTag = "Mediums"
  protected val mutesTag = "Mutes"
  protected val notificationsTag = "Notifications"
  protected val RequestsTag = "Requests"
  protected val sessionsTag = "Sessions"
  protected val socialAccountsTag = "Social Accounts"


}
