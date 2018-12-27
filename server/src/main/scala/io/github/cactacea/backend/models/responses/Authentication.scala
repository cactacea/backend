package io.github.cactacea.backend.models.responses

import io.github.cactacea.backend.core.domain.models.AccountDetail

case class Authentication(
                           account: AccountDetail,
                           accessToken: String
                          )
