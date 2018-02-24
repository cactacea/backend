package io.github.cactacea.core.application.requests.message

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, MediumId}

case class PostMessage(
                        groupId: GroupId,
                        @Size(min = 1, max = 1000)  groupMessage: Option[String],
                        mediumId: Option[MediumId],
                        session: Request
                         )
