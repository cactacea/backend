package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.SocialAccountType

case class SocialAccount(
                       accountType: SocialAccountType,
                       token: String
                   )
