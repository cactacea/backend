package io.github.cactacea.backend.models.requests.setting

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.domain.enums.SocialAccountType

case class DeleteSocialAccount(
                             @RouteParam socialAccountType: SocialAccountType,
                             session: Request
                        )
