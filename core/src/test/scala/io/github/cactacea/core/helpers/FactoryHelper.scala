package io.github.cactacea.backend.core.helpers

import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, ContentStatusType, MediumType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Mediums}

object FactoryHelper {

  def createMediums(accountId: AccountId) = {
    Mediums(
      MediumId(0L),
      "key",
      "http://127.0.0.1/resource/image.jpg",
      120,
      120,
      128L,
      Some("http://127.0.0.1/resource/thumnail_image.jpg"),
      MediumType.image,
      accountId,
      false,
      ContentStatusType.unchecked
    )
  }

  def createAccounts(accountName: String): Accounts = {
    Accounts(
      id = AccountId(0L),
      profileImage = None,
      profileImageUrl = None,
      accountName = accountName,
      displayName = Some("displayname"),
      password = "password",
      accountStatus = AccountStatusType.normally,
      followCount = 0L,
      followerCount = 0L,
      friendCount = 0L,
      web = Some("test@example.com"),
      birthday = None,
      location = Some("location"),
      bio = Some("bio"),
      signedOutAt = None
    )
  }

}
