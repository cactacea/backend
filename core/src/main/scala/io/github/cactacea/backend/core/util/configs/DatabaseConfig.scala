package io.github.cactacea.backend.core.util.configs

import com.twitter.util.Duration

case class DatabaseConfig(
                           database: Option[String],
                           user: Option[String],
                           password: Option[String],
                           dest: Option[String],
                           lowWatermark: Option[Int],
                           highWatermark: Option[Int],
                           idleTime: Option[Duration],
                           bufferSize: Option[Int],
                           maxPrepareStatements: Option[Int],
                           connectTimeout: Option[Int],
                           maxWaiters: Option[Int]
                         )
