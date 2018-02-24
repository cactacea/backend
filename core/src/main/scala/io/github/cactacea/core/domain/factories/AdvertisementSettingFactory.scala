package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.AdvertisementSetting
import io.github.cactacea.core.infrastructure.models.AdvertisementSettings

object AdvertisementSettingFactory {

  def create(s: AdvertisementSettings): AdvertisementSetting = {
    AdvertisementSetting(
      ad1 = s.ad1,
      ad2 = s.ad2,
      ad3 = s.ad3,
      ad4 = s.ad4,
      ad5 = s.ad5
    )
  }
}
