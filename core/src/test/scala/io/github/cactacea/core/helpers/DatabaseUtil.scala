package io.github.cactacea.core.helpers

import io.github.cactacea.core.util.provider.module.DatabaseProviderModule

object DatabaseUtil {

  val db = DatabaseProviderModule.context()

}
