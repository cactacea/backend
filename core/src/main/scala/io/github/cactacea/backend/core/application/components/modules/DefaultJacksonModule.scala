package io.github.cactacea.backend.core.application.components.modules

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.twitter.finatra.json.modules.FinatraJacksonModule

object DefaultJacksonModule extends FinatraJacksonModule {
  override val propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
}
