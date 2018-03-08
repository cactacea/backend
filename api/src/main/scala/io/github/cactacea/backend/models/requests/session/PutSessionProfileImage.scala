package io.github.cactacea.backend.models.requests.session

import com.twitter.finagle.http.Request
import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class PutSessionProfileImage(
                                           mediumId: Option[MediumId],
                                           session: Request
                                    )
