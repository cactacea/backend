package io.github.cactacea.backend.server

import io.github.cactacea.backend.core.domain.models.User

case class Authentication(
                           user: User,
                           accessToken: String
                          )
