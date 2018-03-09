package io.github.cactacea.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.SocialAccountsService
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.util.clients.facebook.FacebookClient
import io.github.cactacea.util.clients.google.GoogleClient
import io.github.cactacea.util.clients.twitter.TwitterClient

class DefaultSocialAccountsService extends SocialAccountsService {

  @Inject private var facebookClient: FacebookClient = _
  @Inject private var twitterClient: TwitterClient = _
  @Inject private var googleClient: GoogleClient = _

  def get(socialAccountType: String, accessToken: String, accessTokenSecret: String): Future[Option[String]] = {
    socialAccountType match {
      case "facebook" =>
        facebookClient.me(accessToken).map(s => Some(s.id))
      case "twitter" =>
        twitterClient.me(accessToken, accessTokenSecret).map(s => Some(s.id))
      case "google" =>
        googleClient.me(accessToken).map(s => Some(s.id))
      case _ =>
        Future.None
    }
  }

}

