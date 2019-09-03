package io.github.cactacea.backend.server.helpers

import com.twitter.finagle.http.Request
import io.github.cactacea.backend.auth.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.core.helpers.generators.{StatusGenerator, ValueGenerator}
import io.github.cactacea.backend.core.util.configs.Config
import org.scalacheck.Gen

trait RequestGenerator extends ValueGenerator with StatusGenerator {

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
  } yield (PostSignUp(userName, password, Request()))


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
