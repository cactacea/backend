package io.github.cactacea.backend.models.requests.setting

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam


case class DeleteSocialAccount(
                             @RouteParam socialAccountType: String,
                             session: Request
                        )
