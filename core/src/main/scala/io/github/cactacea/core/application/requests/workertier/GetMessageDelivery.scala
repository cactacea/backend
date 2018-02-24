package io.github.cactacea.core.application.requests.workertier

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.MessageId

case class GetMessageDelivery(@RouteParam messageId: MessageId)
