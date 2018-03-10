package io.github.cactacea.backend.models.requests.message

import com.twitter.finatra.validation.Size
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, MediumId}

case class PostMessage(
                        id: GroupId,
                        @Size(min = 1, max = 1000)  groupMessage: Option[String],
                        mediumId: Option[MediumId]
                         )
