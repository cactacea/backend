package io.github.cactacea.backend.models.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.Size

case class PutSessionPassword(
                           @Size(min = 1, max = 255) oldPassword: String,
                           @Size(min = 1, max = 255) newPassword: String,
                           session: Request
)
