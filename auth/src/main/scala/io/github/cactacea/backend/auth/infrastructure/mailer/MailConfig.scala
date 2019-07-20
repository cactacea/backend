package io.github.cactacea.backend.auth.infrastructure.mailer

case class MailConfig(
                         address: Option[String],
                         forgotPasswordLink: Option[String],
                         welcomeLink: Option[String])
