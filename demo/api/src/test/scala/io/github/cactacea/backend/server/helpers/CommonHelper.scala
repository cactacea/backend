package io.github.cactacea.backend.server.helpers

import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.APIServerSpec

trait CommonHelper {
  self: APIServerSpec =>

  private val agent =
    "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) " +
      "Version/11.0 Mobile/15E148 Safari/604.1"

  def headers(): Map[String, String] = {
    val (_, s) = Config.auth.keys.all.head
    Map(
      Config.auth.headerNames.apiKey -> s,
      "User-Agent" -> agent
    )
  }

  def headers(accessToken: String): Map[String, String] = {
    val (_, s) = Config.auth.keys.all.head
    Map(
      Config.auth.headerNames.apiKey -> s,
      Config.auth.headerNames.authorizationKey -> accessToken,
      "User-Agent" -> agent,
    )
  }

}
