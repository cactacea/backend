package io.github.cactacea.core.util.tokens

import java.util.Date

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.jsonwebtoken._

@Singleton
class OAuthTokenGenerator @Inject()(config: ConfigService) {

  def generateCode(): String = {
    generate(60 * 3)
  }

  def generateAccessToken(): String = {
    generate(60 * 15)
  }

  def generateRefreshToken(): String = {
    generate(60 * 60 * 30)
  }

  private def generate(expireIn: Long): String = {

    val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
    val expired = new Date(System.nanoTime() + expireIn)
    val claims = new java.util.HashMap[String, Object]()

    Jwts.builder()
      .setIssuer(config.issuer)
      .setIssuedAt(new Date())
      .setSubject(config.subject)
      .setClaims(claims)
      .setExpiration(expired)
      .signWith(signatureAlgorithm, config.signingKey)
      .compact()

  }

  def parse(token: String): Future[Option[Boolean]] = {

    try {

      val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
      val jwtClaims = Jwts.parser()
        .setSigningKey(config.signingKey)
        .parseClaimsJws(token)
      val header = jwtClaims.getHeader()
      val body = jwtClaims.getBody()

      if (header.getAlgorithm().equals(signatureAlgorithm.getValue) == false) {
        Future.None

      } else if (body.getSubject.equals(config.subject) == false) {
        Future.None

      } else if (body.getIssuer.equals(config.issuer) == false) {
        Future.None

      } else {
        Future.value(Some(true))
      }

    } catch {
      case _: Exception => Future.None
    }

  }

}

