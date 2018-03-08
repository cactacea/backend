package io.github.cactacea.backend.models.requests.message

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class GetMessages(
                        @QueryParam groupId: GroupId,
                        @QueryParam since: Option[Long],
                        @QueryParam offset: Option[Int],
                        @QueryParam @Max(50) count: Option[Int],
                        @QueryParam ascending: Boolean
                       )
