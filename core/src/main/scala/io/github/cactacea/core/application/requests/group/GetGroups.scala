package io.github.cactacea.core.application.requests.group

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.{Max, Size}
import io.github.cactacea.core.domain.enums.GroupPrivacyType

case class GetGroups(
                      @QueryParam @Size(min = 0, max = 1000) groupName: Option[String],
                      @QueryParam byInvitationOnly: Option[Boolean],
                      @QueryParam privacyType: Option[GroupPrivacyType],
                      @QueryParam since: Option[Long],
                      @QueryParam offset: Option[Int],
                      @QueryParam @Max(50) count: Option[Int],
                      session: Request
                     )
