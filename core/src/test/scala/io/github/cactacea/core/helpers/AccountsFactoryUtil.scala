package io.github.cactacea.core.helpers

import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.github.cactacea.core.infrastructure.models.Accounts

object AccountsFactoryUtil {

  def create(accountName: Long): Accounts = {
    Accounts(
      id = AccountId(0L),
      profileImage = None,
      profileImageUrl = None,
      accountName = accountName.toString,
      displayName = "displayname",
      password = "password",
      position = 0L,
      accountStatus = 0L,
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
