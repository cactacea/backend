package io.github.cactacea.backend.core.util.configs

case class DatabaseConfig(
                           database: Option[String],
                           user: Option[String],
                           password: Option[String],
                           hostname: Option[String],
                           port: Option[String],
                           poolWatermarkLow: Option[Long],
                           poolWatermarkMax: Option[Long]
                         ) {

}
