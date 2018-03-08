package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.request.RouteParam


case class DeleteSocialAccount(
                             @RouteParam socialAccountType: String
                        )
