package io.github.cactacea.backend.models.requests.workertier

import com.twitter.finatra.request.{QueryParam, RouteParam}
import io.github.cactacea.core.infrastructure.identifiers.{FeedId, SessionId}

case class GetFeedDelivery(@RouteParam feedId: FeedId)
