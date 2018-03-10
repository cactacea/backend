package io.github.cactacea.backend.models.requests.message

import com.twitter.finatra.request.FormParam
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class DeleteMessages (
                            @FormParam id: GroupId
                          )
