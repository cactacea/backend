package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation._

case class GetAccounts(
                      @QueryParam @Size(min = 0, max = 1000)
                      displayName: Option[String],

                      @QueryParam
                      since: Option[Long],

                      @QueryParam
                      offset: Option[Int],

                      @QueryParam
                      @Max(50)
                      count: Option[Int]
                      )