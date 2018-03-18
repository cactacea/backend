package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.validation.Size
import io.swagger.annotations.{ApiImplicitParam, ApiImplicitParams, ApiModelProperty}

case class PutSessionProfile (
                               @ApiModelProperty(value = "Display name.")
                               @Size(min = 1, max = 50) displayName: String,

                               @ApiModelProperty(value = "Profile URL.")
                               @Size(min = 0, max = 2038) web: Option[String],

                               @ApiModelProperty(value = "Account birthday.")
                               birthday: Option[Long],

                               @ApiModelProperty(value = "Account address.")
                               @Size(min = 0, max = 255) location: Option[String],

                               @ApiModelProperty(value = "Account bio.")
                               @Size(min = 0, max = 1024) bio: Option[String],
                             )
