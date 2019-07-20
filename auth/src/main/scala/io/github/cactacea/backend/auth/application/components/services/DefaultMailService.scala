package io.github.cactacea.backend.auth.application.components.services

import com.twitter.util.Future
import io.github.cactacea.backend.auth.application.components.interfaces.MailService
import io.github.cactacea.filhouette.api.Logger

class DefaultMailService extends MailService with Logger {

  def send(addressText: String, toText: String, subjectText: String, bodyText: Option[String], bodyHtml: Option[String]): Future[Unit] = {
    info(bodyText.getOrElse(subjectText))
    Future.Done
  }

}
