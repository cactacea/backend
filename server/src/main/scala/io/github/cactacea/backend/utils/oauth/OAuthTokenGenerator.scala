package io.github.cactacea.backend.utils.oauth

import java.util.Date

import com.google.inject.Singleton
import io.github.cactacea.backend.core.util.configs.Config
import io.jsonwebtoken._
import org.joda.time.DateTime

@Singleton
class OAuthTokenGenerator {

  def generateToken(audience: Long, clientId: String, scope: String): String = {
    val expired = 30 * 60 // 30 minutes
    generate("token", audience, clientId, scope, expired)
  }

  def generateRefreshToken(audience: Long, clientId: String, scope: String): String = {
    val expired = 60 * 60 * 24 * 30 // 30 days
    generate("refreshToken", audience, clientId, scope, expired)
  }

  private def generate(subject: String, audience: Long, clientId: String, scope: String, expiration: Long): String = {
    val expired = new DateTime().plusMinutes(expiration).toDate
    val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)

    Jwts.builder()
      .setIssuer(Config.auth.token.issuer)
      .setIssuedAt(new Date())
      .setSubject(subject)
      .setHeaderParam("client_id", clientId)
      .setHeaderParam("scope", scope)
      .setHeaderParam("expired", expired.toString)
      .setExpiration(expired)
      .setAudience(audience.toString())
      .signWith(signatureAlgorithm, Config.auth.token.signingKey)
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

      val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
      val parsed = Jwts.parser().setSigningKey(Config.auth.token.signingKey).parseClaimsJws(token)
      val header = parsed.getHeader()
      val body = parsed.getBody()
      val issuedAt = body.getIssuedAt
      val clientId = header.getOrDefault("client_id", "").asInstanceOf[String]
      val scope = header.getOrDefault("scope", "").asInstanceOf[String]
      val expired = header.getOrDefault("expired", "0").asInstanceOf[String].toLong
      val audience = body.getAudience().toLong

      if (header.getAlgorithm().equals(signatureAlgorithm.getValue)
            && body.getSubject.equals(subject)
            && body.getIssuer.equals(Config.auth.token.issuer)) {
        Some(OAuthParsedToken(audience, issuedAt, expired, clientId, Some(scope)))
      } else {
        None
      }

    } catch {
      case _: JwtException =>
        None

    }

  }

}

