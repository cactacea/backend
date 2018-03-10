package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.FormParam
import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class PutSessionProfileImage(
                                   id: MediumId
                                    )
