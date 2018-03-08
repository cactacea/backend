package io.github.cactacea.backend.models.requests.setting

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.Size

case class PostDeviceToken(
                            @Size(min = 1, max = 1000) pushToken: Option[String],
                            session: Request
                                  )
