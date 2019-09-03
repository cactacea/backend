package io.github.cactacea.backend.server.utils.authorizations

import com.twitter.finagle.http.Request
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.oauth.Scope
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

case class CactaceaAuthorization(scope: Scope) extends Authorization[Authentication, JWTAuthenticator] {

  def isAuthorized(user: Authentication, authenticator: JWTAuthenticator)(implicit request: Request): Future[Boolean] = {
    CactaceaContext.scope.fold(Future.True)({ s =>
      Future.value(s.split(' ').contains(scope.value))
    })
  }

}

object CactaceaAuthorization {

  val basic: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.basic)
  val feeds: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.feeds)
  val feedLikes: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.feedLikes)
  val comments: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.comments)
  val commentLikes: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.commentLikes)
  val channels: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.channels)
  val invitations: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.invitations)
  val messages: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.messages)
  val followerList: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.followerList)
  val relationships: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.relationships)
  val media: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.media)
  val reports: Authorization[Authentication, JWTAuthenticator] = CactaceaAuthorization(Scope.reports)

}


