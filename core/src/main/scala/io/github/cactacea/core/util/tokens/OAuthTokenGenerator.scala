package io.github.cactacea.core.util.tokens

import java.util.Date

import com.twitter.util.Future
import io.getquill.util.LoadConfig
import io.jsonwebtoken._

object OAuthTokenGenerator {

  private lazy val config = LoadConfig("auth")
  private lazy val signingKey = config.getString("signingKey")
  private lazy val issuer = config.getString("issuer")
  private lazy val subject = config.getString("subject")
  private lazy val algorithm = config.getString("algorithm")

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

    val signatureAlgorithm = SignatureAlgorithm.forName(algorithm)
    val expired = new Date(System.nanoTime() + expireIn)
    val claims = new java.util.HashMap[String, Object]()

    Jwts.builder()
      .setIssuer(issuer)
      .setIssuedAt(new Date())
      .setSubject(subject)
      .setClaims(claims)
      .setExpiration(expired)
      .signWith(signatureAlgorithm, signingKey)
      .compact()

  }

  def parse(token: String): Future[Option[Boolean]] = {

    try {

      val signatureAlgorithm = SignatureAlgorithm.forName(algorithm)
      val jwtClaims = Jwts.parser()
        .setSigningKey(signingKey)
        .parseClaimsJws(token)
      val header = jwtClaims.getHeader()
      val body = jwtClaims.getBody()

      if (header.getAlgorithm().equals(signatureAlgorithm.getValue) == false) {
        Future.None

      } else if (body.getSubject.equals(subject) == false) {
        Future.None

      } else if (body.getIssuer.equals(issuer) == false) {
        Future.None

      } else {
        Future.value(Some(true))
      }

    } catch {
      case _: Exception => Future.None
    }

  }

}

