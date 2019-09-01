package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, DeviceType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, DeviceId}

case class Devices(
                    id: DeviceId,
                    userId: UserId,
                    udid: String,
                    deviceType: DeviceType,
                    activeStatus: ActiveStatusType,
                    pushToken: Option[String],
                    userAgent: Option[String],
                    registeredAt: Long
              )
