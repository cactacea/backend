package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.identifiers.{FeedId, MediumId}

case class PutFeed(
                    @RouteParam id: FeedId,
                    @Size(min = 1, max = 1000) feedMessage : String,
                    mediumIds : Option[Array[MediumId]],
                    @Size(min = 0, max = 1000) tags : Option[Array[String]],
                    privacyType : FeedPrivacyType,
                    contentWarning : Boolean,
                    expiration: Option[Long]
                    )