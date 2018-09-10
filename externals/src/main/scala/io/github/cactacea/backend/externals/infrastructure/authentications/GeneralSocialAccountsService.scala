package io.github.cactacea.backend.externals.infrastructure.authentications

import io.github.cactacea.backend.core.application.components.services.DefaultSocialAccountsService

class GeneralSocialAccountsService extends DefaultSocialAccountsService {

  override val services = Map(
    "faceook" -> new FacebookService(),
    "google" -> new GooglePlusService(),
    "twitter" -> new TwitterService(),
  )

}
