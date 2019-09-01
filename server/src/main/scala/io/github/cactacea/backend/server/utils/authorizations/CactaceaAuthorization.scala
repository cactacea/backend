package io.github.cactacea.backend.server.utils.authorizations

import com.twitter.finagle.http.Request
import com.twitter.util.Future
import io.github.cactacea.backend.auth.domain.models.Auth
import io.github.cactacea.backend.oauth.Scope
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

case class CactaceaAuthorization(scope: Scope) extends Authorization[Auth, JWTAuthenticator] {

  def isAuthorized(user: Auth, authenticator: JWTAuthenticator)(implicit request: Request): Future[Boolean] = {
    CactaceaContext.scope.fold(Future.True)({ s =>
      Future.value(s.split(' ').contains(scope.value))
    })
  }

}

object CactaceaAuthorization {

  val basic: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.basic)
  val feeds: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.feeds)
  val feedLikes: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.feedLikes)
  val comments: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.comments)
  val commentLikes: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.commentLikes)
  val channels: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.channels)
  val invitations: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.invitations)
  val messages: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.messages)
  val followerList: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.followerList)
  val relationships: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.relationships)
  val media: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.media)
  val reports: Authorization[Auth, JWTAuthenticator] = CactaceaAuthorization(Scope.reports)

}


