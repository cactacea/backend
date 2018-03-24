package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

case class AdvertisementSettings(
                                  accountId: AccountId,
                                  ad1: Boolean,
                                  ad2: Boolean,
                                  ad3: Boolean,
                                  ad4: Boolean,
                                  ad5: Boolean)
