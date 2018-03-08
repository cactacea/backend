package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.DeviceType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, DeviceId}

case class Devices(
                    id: DeviceId,
                    accountId: AccountId,
                    udid: String,
                    deviceType: DeviceType,
                    pushToken: Option[String],
                    userAgent: Option[String]
              )
