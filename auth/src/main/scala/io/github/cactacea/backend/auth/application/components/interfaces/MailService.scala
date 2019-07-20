package io.github.cactacea.backend.auth.application.components.interfaces

import com.twitter.util.Future

trait MailService {

  def send(address: String, toText: String, subjectText: String, bodyText: Option[String], bodyHtml: Option[String]): Future[Unit]

}
