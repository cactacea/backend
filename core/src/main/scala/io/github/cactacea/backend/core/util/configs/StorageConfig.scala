package io.github.cactacea.backend.core.util.configs

import com.twitter.util.StorageUnit

case class StorageConfig (
                           hostName: Option[String],
                           port: Option[String],
                           maxFileSize: Option[StorageUnit]
                         )
