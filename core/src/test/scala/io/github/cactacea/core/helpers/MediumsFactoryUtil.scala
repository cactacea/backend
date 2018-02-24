package io.github.cactacea.core.helpers

import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId}
import io.github.cactacea.core.infrastructure.models.Mediums

object MediumsFactoryUtil {

  def create(accountId: AccountId) = {
    Mediums(
      MediumId(0L),
      "key",
      "http://127.0.0.1/resource/image.jpg",
      120,
      120,
      128L,
      Some("http://127.0.0.1/resource/thumnail_image.jpg"),
      MediumType.image.toValue,
      accountId
    )
  }
}
