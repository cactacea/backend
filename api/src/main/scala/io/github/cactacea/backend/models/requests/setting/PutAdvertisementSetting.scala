package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.request.FormParam

case class PutAdvertisementSetting(
                                    ad1: Boolean,
                                    ad2: Boolean,
                                    ad3: Boolean,
                                    ad4: Boolean,
                                    ad5: Boolean
                                    )