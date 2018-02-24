package io.github.cactacea.core.application.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.{PastTime, Size}
import org.joda.time.DateTime

case class PutSessionProfile (
                               @Size(min = 1, max = 50) displayName: String,
                               @Size(min = 0, max = 2038) web: Option[String],
                               @PastTime birthday: Option[DateTime],
                               @Size(min = 0, max = 255) location: Option[String],
                               @Size(min = 0, max = 1024) bio: Option[String],
                               session: Request
                             )
