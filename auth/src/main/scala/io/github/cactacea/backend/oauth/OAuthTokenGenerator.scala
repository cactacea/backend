package io.github.cactacea.backend.oauth

import java.util.Date

import io.github.cactacea.backend.auth.enums.OAuthTokenType
import io.github.cactacea.backend.core.util.configs.Config
import io.jsonwebtoken.{JwtException, Jwts, SignatureAlgorithm}

object OAuthTokenGenerator {

  def generate(tokenType: OAuthTokenType, userName: String, clientId: String, scope: Option[String], redirectUri: Option[String], expiration: Long): String = {
    val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
    Jwts.builder()
      .setIssuer(Config.auth.token.issuer)
      .setIssuedAt(new Date())
      .setSubject(tokenType.toValue)
      .setHeaderParam("client_id", clientId)
      .setHeaderParam("expiration", expiration)
      .setHeaderParam("scope", scope.getOrElse(""))
      .setHeaderParam("redirect_uri", redirectUri.getOrElse(""))
      .setHeaderParam("expiration", expiration)
      .setAudience(userName)
      .signWith(signatureAlgorithm, Config.auth.token.signingKey)
      .compact()
  }

  def parse(tokenType: OAuthTokenType, token: String): Option[OAuthToken] = {
    try {
      val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
      val parsed = Jwts.parser().setSigningKey(Config.auth.token.signingKey).parseClaimsJws(token)
      val header = parsed.getHeader()
      val body = parsed.getBody()
      val issuedAt = body.getIssuedAt
      val clientId = header.getOrDefault("client_id", "").asInstanceOf[String]
      val expiration = header.getOrDefault("expiration", "0").asInstanceOf[Long]
      val scope = header.getOrDefault("scope", "").asInstanceOf[String]
      val redirectUri = header.getOrDefault("redirect_uri", "").asInstanceOf[String]
      val audience = body.getAudience()
      if (header.getAlgorithm().equals(signatureAlgorithm.getValue)
        && body.getSubject.equals(tokenType.toValue)
        && body.getIssuer.equals(Config.auth.token.issuer)) {
        Option(OAuthToken(audience, issuedAt, Option(expiration), clientId, Option(redirectUri) ,Option(scope)))
      } else {
        None
      }
    } catch {
      case _: JwtException =>
        None
    }
  }

}
