package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.AccountStatusType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId}

case class Accounts(
                     id: AccountId,
                     accountName: String,
                     displayName: String,
                     profileImage: Option[MediumId],
                     profileImageUrl: Option[String],
                     password: String,
                     followCount: Long,
                     followerCount: Long,
                     friendCount: Long,
                     web: Option[String],
                     birthday: Option[Long],
                     location: Option[String],
                     bio: Option[String],
                     accountStatus: AccountStatusType,
                     signedOutAt: Option[Long]
                 ) {


  def isTerminated: Boolean = {
    this.accountStatus == AccountStatusType.terminated
  }

  def isExpired(expiredDate: Long): Boolean = {
    this.signedOutAt.map(_ > expiredDate).getOrElse(false)
  }

}
