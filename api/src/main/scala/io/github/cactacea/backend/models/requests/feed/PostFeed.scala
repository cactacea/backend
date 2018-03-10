package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.FormParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class PostFeed(
                     @Size(min = 1, max = 1000) feedMessage : String,
                     mediumIds : Option[Array[MediumId]],
                     @Size(min = 0, max = 1000) tags : Option[Array[String]],
                     privacyType : FeedPrivacyType,
                     contentWarning : Boolean,
                     expiration: Option[Long]
                       )
