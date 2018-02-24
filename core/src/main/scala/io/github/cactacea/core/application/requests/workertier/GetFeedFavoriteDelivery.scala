package io.github.cactacea.core.application.requests.workertier

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class GetFeedFavoriteDelivery (@RouteParam feedId: FeedId)
