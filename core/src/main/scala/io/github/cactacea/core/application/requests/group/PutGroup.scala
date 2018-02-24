package io.github.cactacea.core.application.requests.group

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{FormParam, RouteParam}
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class PutGroup (
                      @RouteParam groupId: GroupId,
                      @Size(min = 1, max = 1000) groupName: String,
                      byInvitationOnly: Boolean,
                      privacyType: GroupPrivacyType,
                      authorityType: GroupAuthorityType,
                      session: Request
                     )
