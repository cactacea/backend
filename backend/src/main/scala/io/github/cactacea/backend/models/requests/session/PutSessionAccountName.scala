package io.github.cactacea.backend.models.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.validation.Size

case class PutSessionAccountName(
                           @Size(min = 2, max = 30) accountName: String,
                           session: Request
                    )