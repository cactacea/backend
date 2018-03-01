package io.github.cactacea.backend.models.requests.session

import com.twitter.finagle.http.Request

case class GetSessionAccount(
                           session: Request
                         )

