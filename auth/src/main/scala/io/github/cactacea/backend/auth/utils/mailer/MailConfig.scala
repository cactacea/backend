package io.github.cactacea.backend.auth.utils.mailer

case class MailConfig(
                         address: Option[String],
                         forgotPasswordLink: Option[String],
                         welcomeLink: Option[String])
