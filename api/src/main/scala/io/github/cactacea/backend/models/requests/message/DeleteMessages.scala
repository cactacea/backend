package io.github.cactacea.backend.models.requests.message

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.FormParam
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class DeleteMessages (
                            @FormParam groupId: GroupId,
                            session: Request
                          )
