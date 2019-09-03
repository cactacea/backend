package io.github.cactacea.backend.auth.core.utils.mailer

case class MailConfig(
                         address: Option[String],
                         forgotPasswordLink: Option[String],
                         welcomeLink: Option[String])
