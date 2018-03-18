package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.ActiveStatus
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class AccountStatus (
                           id: AccountId,
                           status: ActiveStatus)
