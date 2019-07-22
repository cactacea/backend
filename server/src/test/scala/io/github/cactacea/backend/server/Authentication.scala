package io.github.cactacea.backend.server

import io.github.cactacea.backend.core.domain.models.Account

case class Authentication(
                           account: Account,
                           accessToken: String
                          )
