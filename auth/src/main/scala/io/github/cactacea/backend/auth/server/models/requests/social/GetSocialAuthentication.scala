package io.github.cactacea.backend.auth.server.models.requests.social

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty

case class GetSocialAuthentication(
                       @ApiModelProperty(value = "Provider type.", required = true)
                       @RouteParam provider: String,

                       @JsonIgnore
                       @ApiModelProperty(hidden = true)
                       request: Request

                     ) {

}
