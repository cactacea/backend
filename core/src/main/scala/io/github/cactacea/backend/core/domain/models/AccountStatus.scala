package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.ActiveStatus
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class AccountStatus (
                           id: AccountId,
                           status: ActiveStatus)
