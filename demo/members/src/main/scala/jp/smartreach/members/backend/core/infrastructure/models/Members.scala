package jp.smartreach.members.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.smartreach.members.backend.core.domain.enums.CommunicationType
import jp.smartreach.members.backend.core.infrastructure.identifiers.MemberId

case class Members (
                     id: MemberId,
                     name: Option[String],
                     address: Option[String],
                     city: Option[String],
                     state: Option[String],
                     zip: Option[String],
                     communicationType: CommunicationType,
                     userId: Option[UserId],
                     email: Option[String],
                     phoneNo: Option[String],
                     registeredAt: Long
                   )
