package io.github.cactacea.backend.models.requests.setting

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.domain.enums.SocialAccountType

case class PostSocialAccount(
                              @RouteParam socialAccountType: SocialAccountType,
                              @Size(min = 1, max = 1000) accessToken: String,
                              session: Request
                                 )
