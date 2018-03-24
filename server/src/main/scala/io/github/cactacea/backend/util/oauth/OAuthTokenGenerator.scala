package io.github.cactacea.backend.util.oauth

import java.util.Date

import com.google.inject.{Inject, Singleton}
import io.github.cactacea.backend.core.application.components.interfaces.ConfigService
import io.jsonwebtoken._

@Singleton
class OAuthTokenGenerator {

  @Inject var config: ConfigService = _

  def generateToken(audience: Long, clientId: String, scope: String): String = {
    val expired = 30 * 60 // 30 minutes
    generate("token", audience, clientId, scope, expired)
  }

  def generateRefreshToken(audience: Long, clientId: String, scope: String): String = {
    val expired = 60 * 60 * 24 * 30 // 30 days
    generate("refreshToken", audience, clientId, scope, expired)
  }

  private def generate(subject: String, audience: Long, clientId: String, scope: String, expired: Long): String = {

    val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)

    Jwts.builder()
      .setIssuer(config.issuer)
      .setIssuedAt(new Date())
      .setSubject(subject)
      .setHeaderParam("client_id", clientId)
      .setHeaderParam("scope", scope)
      .setHeaderParam("expired", expired.toString)
      .setAudience(audience.toString())
      .signWith(signatureAlgorithm, config.signingKey)
      .compact()

  }


  def parseToken(token: String): Option[OAuthParsedToken] = {
    parse("token", token)
  }

  def parseRefreshToken(refreshToken: String): Option[OAuthParsedToken] = {
    parse("refreshToken", refreshToken)
  }

  private def parse(subject: String, token: String): Option[OAuthParsedToken] = {

    try {

      val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
      val parsed = Jwts.parser().setSigningKey(config.signingKey).parseClaimsJws(token)
      val header = parsed.getHeader()
      val body = parsed.getBody()
      val issuedAt = body.getIssuedAt
      val clientId = header.getOrDefault("client_id", "").asInstanceOf[String]
      val scope = header.getOrDefault("scope", "").asInstanceOf[String]
      val expired = header.getOrDefault("expired", "0").asInstanceOf[String].toLong
      val audience = body.getAudience().toLong

      if (header.getAlgorithm().equals(signatureAlgorithm.getValue) == false) {
        None

      } else if (body.getSubject.equals(subject) == false) {
        None

      } else if (body.getIssuer.equals(config.issuer) == false) {
        None

      } else {
        Some(OAuthParsedToken(audience, issuedAt, expired, clientId, Some(scope)))
      }

    } catch {
      case _: Exception =>
        None

    }

  }

}

