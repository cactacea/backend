package io.github.cactacea.backend.models.requests.group

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.FormParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class PostGroup (
                       @Size(min = 1, max = 1000) groupName: String,
                       byInvitationOnly: Boolean,
                       privacyType: GroupPrivacyType,
                       authorityType: GroupAuthorityType,
                       session: Request
                     )
