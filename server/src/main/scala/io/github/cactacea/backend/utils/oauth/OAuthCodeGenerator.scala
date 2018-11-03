package io.github.cactacea.backend.utils.oauth

import java.util.Date

import com.google.inject.Singleton
import io.github.cactacea.backend.core.util.configs.Config
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import org.joda.time.DateTime

@Singleton
class OAuthCodeGenerator {

  def generateCode(audience: Long, clientId: String, scope: String): String = {
    val expired = new DateTime().plusMinutes(3).toDate
    val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
    val jwt = Jwts.builder()
      .setIssuer(Config.auth.token.issuer)
      .setIssuedAt(new Date())
      .setSubject("code")
      .setHeaderParam("client_id", clientId)
      .setHeaderParam("scope", scope)
      .setAudience(audience.toString())
      .setExpiration(expired)
      .signWith(signatureAlgorithm, Config.auth.token.signingKey)
      .compact()

    jwt
  }

  def parseCode(code: String): Option[OAuthParsedToken] = {

    try {

      val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
      val parsed = Jwts.parser().setSigningKey(Config.auth.token.signingKey).parseClaimsJws(code)
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

      } else if (body.getIssuer.equals(Config.auth.token.issuer) == false) {
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
