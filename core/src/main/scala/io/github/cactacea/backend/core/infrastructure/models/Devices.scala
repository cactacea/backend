package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, DeviceType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, DeviceId}

case class Devices(
                    id: DeviceId,
                    accountId: AccountId,
                    udid: String,
                    deviceType: DeviceType,
                    activeStatus: ActiveStatusType,
                    pushToken: Option[String],
                    userAgent: Option[String]
              )
