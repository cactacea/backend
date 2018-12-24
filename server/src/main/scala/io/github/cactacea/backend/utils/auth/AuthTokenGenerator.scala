package io.github.cactacea.backend.utils.auth

import java.util.Date

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.jsonwebtoken._
import org.joda.time.DateTime

@Singleton
class AuthTokenGenerator {

  def generate(audience: Long, udid: String): String = {

    val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
    val expired = new DateTime().plusMinutes(Config.auth.token.expire.inMinutes).toDate

    val token = Jwts.builder()
      .setIssuer(Config.auth.token.issuer)
      .setIssuedAt(new Date())
      .setSubject(Config.auth.token.subject)
      .setHeaderParam("udid", udid)
      .setAudience(audience.toString())
      .setExpiration(expired)
      .signWith(signatureAlgorithm, Config.auth.token.signingKey)
      .compact()

    token
  }

  def parse(authorization: Option[String]): Future[SessionUser] = {

    authorization match {
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))

      case Some(token) =>
        try {

          val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
          val parsed = Jwts.parser()
            .setSigningKey(Config.auth.token.signingKey)
            .parseClaimsJws(token)
          val header = parsed.getHeader()
          val body = parsed.getBody()
          val udid = header.get("udid").asInstanceOf[String]
          val expiration = body.getExpiration.getTime
          val audience = body.getAudience().toLong

          if (header.getAlgorithm().equals(signatureAlgorithm.getValue)
              && body.getSubject.equals(Config.auth.token.subject)
              && body.getIssuer.equals(Config.auth.token.issuer)) {
            Future.value(SessionUser(SessionId(audience), udid, expiration))
          } else {
            Future.exception(CactaceaException(CactaceaErrors.SessionNotAuthorized))

          }

        } catch {
          case _: ExpiredJwtException =>
            Future.exception(CactaceaException(CactaceaErrors.SessionTimeout))

          case _: JwtException =>
            Future.exception(CactaceaException(CactaceaErrors.SessionNotAuthorized))

        }
    }
  }

  def check(requestApiKey: Option[String]): Future[DeviceType] = {
    requestApiKey.flatMap(key => Config.auth.keys.all.filter({ case (_, k) => k == key}).headOption.map({ case (d, _) => d})) match {
      case Some(t) =>
        Future.value(t)
      case None =>
        Future.exception(CactaceaException(APIKeyIsInValid))
    }
  }

}

