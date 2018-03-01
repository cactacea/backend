package io.github.cactacea.backend.models.requests.workertier

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class GetFeedFavoriteDelivery (@RouteParam feedId: FeedId)
