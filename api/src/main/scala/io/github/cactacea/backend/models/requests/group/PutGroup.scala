package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class PutGroup (
                      @RouteParam id: GroupId,
                      @Size(min = 1, max = 1000) groupName: String,
                      byInvitationOnly: Boolean,
                      privacyType: GroupPrivacyType,
                      authorityType: GroupAuthorityType
                     )
