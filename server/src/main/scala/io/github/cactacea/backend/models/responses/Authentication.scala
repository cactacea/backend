package io.github.cactacea.backend.models.responses

import io.github.cactacea.backend.core.domain.models.Account

case class Authentication(
                           account: Account,
                           accessToken: String
                          )
