package io.github.cactacea.backend.models.requests.workertier

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.MessageId

case class GetMessageDelivery(@RouteParam messageId: MessageId)
