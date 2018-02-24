package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{DeviceId, AccountId}

case class Devices(
                    id: DeviceId,
                    accountId: AccountId,
                    udid: String,
                    pushToken: Option[String],
                    userAgent: Option[String]
              )
