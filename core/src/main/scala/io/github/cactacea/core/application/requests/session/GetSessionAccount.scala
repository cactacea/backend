package io.github.cactacea.core.application.requests.session

import com.twitter.finagle.http.Request

case class GetSessionAccount(
                           session: Request
                         )

