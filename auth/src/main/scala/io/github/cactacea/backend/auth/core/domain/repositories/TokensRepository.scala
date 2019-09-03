package io.github.cactacea.backend.auth.core.domain.repositories

import java.util.Date

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.enums.TokenType
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{InvalidTokenError, TokenAlreadyExpired}
import io.github.cactacea.filhouette.api.LoginInfo
import io.jsonwebtoken.{JwtException, Jwts, SignatureAlgorithm}
import org.joda.time.DateTime

@Singleton
class TokensRepository @Inject()() {

  def issue(providerId: String, providerKey: String, tokenType: TokenType, expiration: Int = 3): Future[String] = {
    val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
    val expired = new DateTime().plusMinutes(expiration).toDate
    val token = Jwts.builder()
      .setIssuer(Config.auth.token.issuer)
      .setIssuedAt(new Date())
      .setSubject(tokenType.toString)
      .setExpiration(expired)
      .setAudience(providerKey)
      .setSubject(providerId)
      .signWith(signatureAlgorithm, Config.auth.token.signingKey)
      .compact()
    Future.value(token)
  }

  def verify(token: String, tokenType: TokenType): Future[LoginInfo] = {
    try {
      val signatureAlgorithm = SignatureAlgorithm.forName(Config.auth.token.algorithm)
      val parsed = Jwts.parser().setSigningKey(Config.auth.token.signingKey).parseClaimsJws(token)
      val header = parsed.getHeader()
      val body = parsed.getBody()
      val audience = body.getAudience()
      val subject = body.getSubject()
      if (header.getAlgorithm().equals(signatureAlgorithm.getValue) &&
          body.getIssuer.equals(Config.auth.token.issuer) &&
          body.getSubject.equals(tokenType.toString)
      ) {
        Future.value(LoginInfo(subject, audience))
      } else {
        Future.exception(CactaceaException(InvalidTokenError))
      }
    } catch {
      case _: JwtException =>
        Future.exception(CactaceaException(TokenAlreadyExpired))
    }
  }

}