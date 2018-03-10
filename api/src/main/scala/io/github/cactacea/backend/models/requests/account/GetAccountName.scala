package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty

case class GetAccountName(
                           @RouteParam @ApiModelProperty(name = "account_name") accountName: String
                     )
