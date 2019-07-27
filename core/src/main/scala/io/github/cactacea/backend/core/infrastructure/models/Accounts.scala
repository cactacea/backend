package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId}

case class Accounts(
                     id: AccountId,
                     accountName: String,
                     displayName: String,
                     profileImage: Option[MediumId],
                     profileImageUrl: Option[String],
                     followCount: Long,
                     followerCount: Long,
                     friendCount: Long,
                     feedCount: Long,
                     web: Option[String],
                     birthday: Option[Long],
                     location: Option[String],
                     bio: Option[String],
                     accountStatus: AccountStatusType,
                     signedOutAt: Option[Long]
                 ) {

}
