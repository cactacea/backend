package io.github.cactacea.backend.components.authentications

import io.github.cactacea.core.application.components.services.DefaultSocialAccountsService

class GeneralSocialAccountsService extends DefaultSocialAccountsService {

  override val services = Map(
    "faceook" -> new FacebookService(),
    "google" -> new GooglePlusService(),
    "twitter" -> new TwitterService(),
  )

}
