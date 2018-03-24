package io.github.cactacea.backend.util.auth

import java.util.Date

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ConfigService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.jsonwebtoken._
import org.joda.time.DateTime

@Singleton
class AuthTokenGenerator @Inject()(config: ConfigService) {

  def generate(audience: Long, udid: String): String = {

    val signatureAlgorithm = SignatureAlgorithm.forName(config.algorithm)
    val expired = new DateTime().plusMinutes(config.expire).toDate

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
          val expiration = body.getExpiration.getTime
          val audience = body.getAudience().toLong

          if (header.getAlgorithm().equals(signatureAlgorithm.getValue) == false) {
            Future.exception(CactaceaException(CactaceaErrors.SessionNotAuthorized))

          } else if (body.getSubject.equals(config.subject) == false) {
            Future.exception(CactaceaException(CactaceaErrors.SessionNotAuthorized))

          } else if (body.getIssuer.equals(config.issuer) == false) {
            Future.exception(CactaceaException(CactaceaErrors.SessionNotAuthorized))

          } else {
            Future.value(SessionUser(SessionId(audience), udid, expiration))
          }

        } catch {
          case _: ExpiredJwtException =>
            Future.exception(CactaceaException(CactaceaErrors.SessionTimeout))

          case _: JwtException =>
            Future.exception(CactaceaException(CactaceaErrors.SessionNotAuthorized))

          case _: Exception =>
            Future.exception(CactaceaException(CactaceaErrors.SessionNotAuthorized))

        }
    }
  }

  def check(requestApiKey: Option[String]): Future[DeviceType] = {
    requestApiKey.flatMap(key => config.apiKeys.filter(_._2 == key).headOption.map(_._1)) match {
      case Some(t) =>
        Future.value(t)
      case _ =>
        Future.exception(CactaceaException(APIKeyIsInValid))
    }
  }

}

