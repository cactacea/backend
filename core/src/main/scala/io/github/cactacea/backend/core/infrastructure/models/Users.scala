package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.UserStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, UserId}

case class Users(
                  id: UserId,
                  userName: String,
                  displayName: String,
                  profileImage: Option[MediumId],
                  profileImageUrl: Option[String],
                  followCount: Long,
                  followerCount: Long,
                  friendCount: Long,
                  tweetCount: Long,
                  web: Option[String],
                  birthday: Option[Long],
                  location: Option[String],
                  bio: Option[String],
                  userStatus: UserStatusType,
                  signedOutAt: Option[Long]
                 ) {

}
