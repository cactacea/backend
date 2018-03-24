package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.models.AdvertisementSettings

case class AdvertisementSetting(
                                  ad1: Boolean,
                                  ad2: Boolean,
                                  ad3: Boolean,
                                  ad4: Boolean,
                                  ad5: Boolean
                               )

object AdvertisementSetting {

  def apply(s: AdvertisementSettings): AdvertisementSetting = {
    AdvertisementSetting(
      ad1 = s.ad1,
      ad2 = s.ad2,
      ad3 = s.ad3,
      ad4 = s.ad4,
      ad5 = s.ad5
    )
  }

}