package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.Size

case class PutSessionProfile (
                               @Size(min = 1, max = 50) displayName: String,
                               @Size(min = 0, max = 2038) web: Option[String],
                               birthday: Option[Long],
                               @Size(min = 0, max = 255) location: Option[String],
                               @Size(min = 0, max = 1024) bio: Option[String]
                             )
