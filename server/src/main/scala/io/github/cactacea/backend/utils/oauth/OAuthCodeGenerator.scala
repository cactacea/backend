package io.github.cactacea.backend.utils.oauth

import java.util.Date

import com.google.inject.{Inject, Singleton}
import io.github.cactacea.backend.core.application.components.interfaces.ConfigService
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import org.joda.time.DateTime

@Singleton
class OAuthCodeGenerator {

  @Inject var config: ConfigService = _

  def generateCode(audience: Long, clientId: String, scope: String): String = {
    val expired = new DateTime().plusMinutes(3).toDate
    val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
    val jwt = Jwts.builder()
      .setIssuer(config.issuer)
      .setIssuedAt(new Date())
      .setSubject("code")
      .setHeaderParam("client_id", clientId)
      .setHeaderParam("scope", scope)
      .setAudience(audience.toString())
      .setExpiration(expired)
      .signWith(signatureAlgorithm, config.signingKey)
      .compact()

    jwt
  }

  def parseCode(code: String): Option[OAuthParsedToken] = {

    try {

      val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
      val parsed = Jwts.parser().setSigningKey(config.signingKey).parseClaimsJws(code)
      val header = parsed.getHeader()
      val body = parsed.getBody()
      val issuedAt = body.getIssuedAt
      val clientId = header.getOrDefault("client_id", "").asInstanceOf[String]
      val scope = header.getOrDefault("scope", "").asInstanceOf[String]
      val expiration = body.getExpiration.getTime
      val audience = body.getAudience().toLong

      if (header.getAlgorithm().equals(signatureAlgorithm.getValue) == false) {
        None

      } else if (body.getSubject.equals("code") == false) {
        None

      } else if (body.getIssuer.equals(config.issuer) == false) {
        None

      } else {
        Some(OAuthParsedToken(audience, issuedAt, expiration, clientId, Some(scope)))
      }

    } catch {
      case _: Exception =>
        None

    }

  }

}
