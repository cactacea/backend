package io.github.cactacea.backend.server.utils.filters

import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator


trait CactaceaAuthenticationFilterFactory {
  def create(auth: Authorization[Authentication, JWTAuthenticator]): CactaceaAuthenticationFilter
}
