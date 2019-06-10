package io.github.cactacea.backend.utils.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.utils.auth.{CactaceaTokenGenerator, CactaceaContext}
import io.github.cactacea.backend.utils.oauth.Permissions

@Singleton
class CactaceaTokenFilter @Inject()(sessionsRepository: SessionsRepository) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    CactaceaContext.authenticated match {
      case true =>
        service(request)
      case false =>
        CactaceaTokenGenerator.parse(request.headerMap.get(Config.auth.headerNames.authorizationKey)).flatMap({ auth =>
          val expiresIn = auth.expiresIn
          sessionsRepository.checkAccountStatus(auth.sessionId, expiresIn).flatMap({ _ =>
            CactaceaContext.setAuthenticated(true)
            CactaceaContext.setId(auth.sessionId)
            CactaceaContext.setUdid(auth.sessionUdid)
            CactaceaContext.setPermissions(Permissions.all)
            service(request)
          })
        })
    }
  }

}

