package io.github.cactacea.backend.server.utils.authorizations

import com.twitter.finagle.http.Request
import com.twitter.util.Future
import io.github.cactacea.backend.auth.domain.models.User
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator
import io.github.cactacea.oauth.Scope

case class CactaceaAuthorization(scope: Scope) extends Authorization[User, JWTAuthenticator] {

  def isAuthorized(user: User, authenticator: JWTAuthenticator)(implicit request: Request): Future[Boolean] = {
    CactaceaContext.scope.fold(Future.True)({ s =>
      Future.value(s.split(' ').contains(scope.value))
    })
  }

}

object CactaceaAuthorization {

  val basic: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.basic)
  val feeds: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.feeds)
  val feedLikes: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.feedLikes)
  val comments: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.comments)
  val commentLikes: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.commentLikes)
  val groups: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.groups)
  val invitations: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.invitations)
  val messages: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.messages)
  val followerList: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.followerList)
  val relationships: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.relationships)
  val media: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.media)
  val reports: Authorization[User, JWTAuthenticator] = CactaceaAuthorization(Scope.reports)

}


