package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.{ActiveStatus, DeviceType}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, DeviceId}

case class Devices(
                    id: DeviceId,
                    accountId: AccountId,
                    udid: String,
                    deviceType: DeviceType,
                    activeStatus: ActiveStatus,
                    pushToken: Option[String],
                    userAgent: Option[String]
              )
