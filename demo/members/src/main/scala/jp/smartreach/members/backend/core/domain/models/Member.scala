package jp.smartreach.members.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.smartreach.members.backend.core.domain.enums.CommunicationType
import jp.smartreach.members.backend.core.infrastructure.identifiers.MemberId
import jp.smartreach.members.backend.core.infrastructure.models.Members

case class Member (
                     id: MemberId,
                     name: String,
                     address: String,
                     city: String,
                     state: String,
                     zip: String,
                     communicationType: CommunicationType,
                     userId: Option[UserId],
                     email: Option[String],
                     phoneNo: Option[String]
                   )

object Member {

  def apply(m: Members): Member = {
    Member(
      m.id,
      m.name.getOrElse(""),
      m.address.getOrElse(""),
      m.city.getOrElse(""),
      m.state.getOrElse(""),
      m.zip.getOrElse(""),
      m.communicationType,
      m.userId,
      m.email,
      m.phoneNo
    )
  }

}