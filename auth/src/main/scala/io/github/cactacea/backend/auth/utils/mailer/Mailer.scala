package io.github.cactacea.backend.auth.utils.mailer

import java.util.Locale

import com.google.inject.Inject
import com.osinka.i18n.{Lang, Messages}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.application.components.interfaces.MailService
import javax.inject.Singleton

@Singleton
class Mailer @Inject()(mailService: MailService) {

  def welcome(email: String, token: String, locale: Locale): Future[Unit] = {

    implicit val lang = Lang(locale.getLanguage)

    val subject = Messages("mail.welcome.subject")
    val link = Config.mail.welcomeLink + token
    val bodyText =
      s"""
         |
         |${Messages("mail.welcome.hello", email)}
         |
         |${Messages("mail.welcome.prelink")}
         |
         |${link}
         |
         |${Messages("mail.welcome.postlink")}
         |
         |${Messages("mail.sign")}
       """.stripMargin

    mailService.send(
      email,
      Config.mail.address,
      subject,
      Some(bodyText),
      None
    )
  }

  def forgotPassword(email: String, token: String, locale: Locale): Future[Unit] = {

    implicit val lang = Lang(locale.getLanguage)

    val subject = Messages("mail.forgotpwd.subject")
    val link = Config.mail.forgotPasswordLink + token
    val bodyText =
      s"""
         |${Messages("mail.forgotpwd.prelink")}
         |
         |${link}
         |
         |${Messages("mail.forgotpwd.postlink")}
         |
         |${Messages("mail.sign")}
       """.stripMargin

    mailService.send(
      email,
      Config.mail.address,
      subject,
      Some(bodyText),
      None
    )

  }

}