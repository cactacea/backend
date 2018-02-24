package io.github.cactacea.core.application.requests.setting

import com.twitter.finagle.http.Request

case class GetAdvertisementSetting(
                                    session: Request
                                  )