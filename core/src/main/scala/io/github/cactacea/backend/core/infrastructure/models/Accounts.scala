package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId}

case class Accounts(
                     id: AccountId,
                     accountName: String,
                     displayName: String,
                     profileImage: Option[MediumId],
                     profileImageUrl: Option[String],
                     password: String,
                     followingCount: Long,
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


  def isNormally: Boolean = {
    this.accountStatus == AccountStatusType.normally
  }

  def isTerminated: Boolean = {
    this.accountStatus == AccountStatusType.terminated
  }

  def isExpired(expiredDate: Long): Boolean = {
    this.signedOutAt.map(_ > expiredDate).getOrElse(false)
  }

}
