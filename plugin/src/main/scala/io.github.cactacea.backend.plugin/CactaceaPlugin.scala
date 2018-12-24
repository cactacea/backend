package io.github.cactacea.backend.plugin

import sbt._
import autoImport._

object autoImport {
  val cactaceaFlywayLocation = settingKey[String]("Cactacea classpath for flyway locations property")
}

object CactaceaPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = super.projectSettings ++ Seq(
    cactaceaFlywayLocation := "classpath:db/migration/cactacea",
  )

}
