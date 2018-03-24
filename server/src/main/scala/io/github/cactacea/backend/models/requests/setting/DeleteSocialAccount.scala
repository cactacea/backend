package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.validation.Size
import io.swagger.annotations.ApiModelProperty

case class DeleteSocialAccount(
                              @ApiModelProperty(value = "Social account type.")
                              @Size(min = 1, max = 255) socialAccountType: String

                            )
