package io.github.cactacea.backend.auth.core.utils.mailer

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object Config {

  private val config = ConfigFactory.load()
  private val mailConfig = config.as[MailConfig]("mail")

  object mail {
    lazy val address = mailConfig.address.getOrElse("smartreach@smartreach.github.io")
    lazy val forgotPasswordLink = mailConfig.forgotPasswordLink.getOrElse("http://localhost:4200/reset?token_id=")
    lazy val welcomeLink = mailConfig.welcomeLink.getOrElse("http://localhost:4200/verify?token_id=")
  }

  println(s"mail.address = ${mailConfig.address}")
  println(s"mail.forgotPasswordLink = ${mailConfig.forgotPasswordLink}")
  println(s"mail.welcomeLink = ${mailConfig.welcomeLink}")

}
