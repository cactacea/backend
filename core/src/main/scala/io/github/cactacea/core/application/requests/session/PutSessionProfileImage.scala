package io.github.cactacea.core.application.requests.session

import com.twitter.finagle.http.Request
import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class PutSessionProfileImage(
                                           mediumId: Option[MediumId],
                                           session: Request
                                    )
