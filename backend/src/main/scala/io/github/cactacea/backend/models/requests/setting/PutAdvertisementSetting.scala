package io.github.cactacea.backend.models.requests.setting

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.FormParam
import io.github.cactacea.core.domain.models.AdvertisementSetting

case class PutAdvertisementSetting(
                                    @FormParam advertisementSetting: AdvertisementSetting,
                                    session: Request
                                    )