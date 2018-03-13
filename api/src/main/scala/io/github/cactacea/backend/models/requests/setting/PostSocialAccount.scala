package io.github.cactacea.backend.models.requests.setting

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Size


case class PostSocialAccount(
                              @QueryParam @Size(min = 1, max = 1000) accessTokenKey: String,
                              @QueryParam @Size(min = 1, max = 1000) accessTokenSecret: String
                                 )
