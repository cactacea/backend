package io.github.cactacea.core.util.tokens

import java.util.Date

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.jsonwebtoken._
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.util.auth.SessionUser
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError
import io.github.cactacea.core.util.responses.CactaceaError._

@Singleton
class AuthTokenGenerator @Inject()(config: ConfigService) {

  def generate(audience: Long, udid: String): String = {

    val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
    val expired = new Date(System.nanoTime() + config.expire)

    val token = Jwts.builder()
      .setIssuer(config.issuer)
      .setIssuedAt(new Date())
      .setSubject(config.subject)
      .setHeaderParam("udid", udid)
      .setAudience(audience.toString())
      .setExpiration(expired)
      .signWith(signatureAlgorithm, config.signingKey)
      .compact()

    token
  }

  def parse(authorization: Option[String]): Future[SessionUser] = {

    authorization match {
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))

      case Some(token) =>
        try {

          val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
          val parsed = Jwts.parser()
            .setSigningKey(config.signingKey)
            .parseClaimsJws(token)
          val header = parsed.getHeader()
          val body = parsed.getBody()
          val udid = header.get("udid").asInstanceOf[String]
          val issuedAt = body.getIssuedAt.getTime()
          val audience = body.getAudience().toLong

          if (header.getAlgorithm().equals(signatureAlgorithm.getValue) == false) {
            Future.exception(CactaceaException(CactaceaError.SessionNotAuthorized))

          } else if (body.getSubject.equals(config.subject) == false) {
            Future.exception(CactaceaException(CactaceaError.SessionNotAuthorized))

          } else if (body.getIssuer.equals(config.issuer) == false) {
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

  def checkApiKey(requestApiKey: Option[String]): Future[Unit] = {
    if (requestApiKey == Some(config.apiKey)) {
      Future.Unit
    } else {
      Future.exception(CactaceaException(APIKeyIsInValid))
    }
  }

}

