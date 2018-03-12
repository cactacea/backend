package io.github.cactacea.backend.models.requests.medium

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class DeleteMedium (@RouteParam id: MediumId)