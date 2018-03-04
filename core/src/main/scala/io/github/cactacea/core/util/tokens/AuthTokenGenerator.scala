package io.github.cactacea.core.util.tokens

import java.util.Date

import com.twitter.util.Future
import io.getquill.util.LoadConfig
import io.jsonwebtoken._
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.util.auth.SessionUser
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError
import io.github.cactacea.core.util.responses.CactaceaError._

object AuthTokenGenerator {

  private lazy val config = LoadConfig("auth")
  private lazy val signingKey = config.getString("signingKey")
  private lazy val expire = config.getLong("expire")
  private lazy val issuer = config.getString("issuer")
  private lazy val subject = config.getString("subject")
  private lazy val algorithm = config.getString("algorithm")

  def generate(audience: Long, udid: String): String = {

    val signatureAlgorithm = SignatureAlgorithm.forName(algorithm)
    val expired = new Date(System.nanoTime() + expire)

    val token = Jwts.builder()
      .setIssuer(issuer)
      .setIssuedAt(new Date())
      .setSubject(subject)
      .setHeaderParam("udid", udid)
      .setAudience(audience.toString())
      .setExpiration(expired)
      .signWith(signatureAlgorithm, signingKey)
      .compact()

    token
  }

  def parse(authorization: Option[String]): Future[SessionUser] = {

    authorization match {
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))

      case Some(token) =>
        try {

          val signatureAlgorithm = SignatureAlgorithm.forName(algorithm)
          val parsed = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token)
          val header = parsed.getHeader()
          val body = parsed.getBody()
          val udid = header.get("udid").asInstanceOf[String]
          val issuedAt = body.getIssuedAt.getTime()
          val audience = body.getAudience().toLong

          if (header.getAlgorithm().equals(signatureAlgorithm.getValue) == false) {
            Future.exception(CactaceaException(CactaceaError.SessionNotAuthorized))

          } else if (body.getSubject.equals(subject) == false) {
            Future.exception(CactaceaException(CactaceaError.SessionNotAuthorized))

          } else if (body.getIssuer.equals(issuer) == false) {
            Future.exception(CactaceaException(CactaceaError.SessionNotAuthorized))

          } else {
            Future.value(SessionUser(SessionId(audience), udid, issuedAt))
          }

        } catch {
          case _: ExpiredJwtException =>
            Future.exception(CactaceaException(CactaceaError.SessionTimeout))

          case _: JwtException =>
            Future.exception(CactaceaException(CactaceaError.SessionNotAuthorized))

          case _: Exception =>
            Future.exception(CactaceaException(CactaceaError.SessionNotAuthorized))

        }
    }
  }

  lazy val apiKey = config.getString("apiKey")

  def checkApiKey(requestApiKey: Option[String]): Future[Unit] = {
    requestApiKey match {
      case Some(apiKey) =>
        Future.Unit
      case None =>
        Future.exception(CactaceaException(APIKeyIsInValid))
    }
  }

}

