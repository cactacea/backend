package io.github.cactacea.backend.plugin

import sbt._

object CactaceaPlugin extends AutoPlugin {

  object autoImport {
    val cactaceaFlywayLocation = settingKey[String]("Cactacea classpath for flyway locations property")
  }

  import autoImport._

  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = super.projectSettings ++ Seq(
    cactaceaFlywayLocation := "classpath:db/migration/cactacea",
  )

}
