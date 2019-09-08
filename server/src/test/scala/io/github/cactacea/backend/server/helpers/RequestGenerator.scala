package io.github.cactacea.backend.server.helpers

import com.twitter.finagle.http.Request
import io.github.cactacea.backend.auth.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.generators.{DomainValueGenerator, StatusGenerator}
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.models.requests.feed.{PostFeed, PutFeed}
import io.github.cactacea.backend.server.models.requests.session.PostSession
import org.scalacheck.Gen

trait RequestGenerator extends DomainValueGenerator with StatusGenerator {

  lazy val userAgentGen: Gen[String] = Gen.const(
    "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) " +
      "Version/11.0 Mobile/15E148 Safari/604.1")

  lazy val apiKeyGen: Gen[String] = Gen.oneOf(Config.auth.keys.ios, Config.auth.keys.android, Config.auth.keys.web)

  lazy val headerGen: Gen[Map[String, String]] = for {
    userAgent <- userAgentGen
    apiKey <- apiKeyGen
  } yield (Map(Config.auth.headerNames.apiKey -> apiKey, "User-Agent" -> userAgent))

  lazy val postSignUpGen: Gen[PostSignUp] = for {
    userName <- uniqueUserNameGen
    password <- passwordGen
  } yield (PostSignUp(userName, password, None, Request()))

  lazy val postEveryoneFeedGen: Gen[PostFeed] = for {
    message <- feedMessageTextGen
    contentWarning <- booleanGen
  } yield (PostFeed(message, None, None, FeedPrivacyType.everyone, contentWarning, None))

  lazy val putFollowersFeedGen: Gen[PutFeed] = for {
    message <- feedMessageTextGen
    contentWarning <- booleanGen
  } yield (PutFeed(FeedId(0), message, None, None, FeedPrivacyType.followers, contentWarning, None))

  lazy val putSession: Gen[PostSession] = for {
    userName <- uniqueUserNameGen
    displayname <- uniqueDisplayNameOptGen
  } yield (PostSession(userName, displayname))

  def headers(accessToken: String): Gen[Map[String, String]] = {
    for {
      userAgent <- userAgentGen
      apiKey <- apiKeyGen
    } yield (Map(
      Config.auth.headerNames.apiKey -> apiKey,
      Config.auth.headerNames.authorizationKey -> accessToken,
      "User-Agent" -> userAgent
    ))
  }

}
