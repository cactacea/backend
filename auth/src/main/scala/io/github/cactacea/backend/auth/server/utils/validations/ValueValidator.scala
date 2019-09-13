package io.github.cactacea.backend.auth.server.utils.validations

import com.twitter.finatra.validation.ValidationResult

import scala.util.matching.Regex

object ValueValidator {

  def validateEmail(email: String): ValidationResult = {
    validate(
      Option(email),
      "Email must be acceptable email address syntax according to RFC.",
      """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    )
  }

  def validateUserName(userName: String): ValidationResult = {
    validate(
      Option(userName),
      "User name must be alphabet, numeric, period and underscore and its length between 2 to 30.",
      """^[A-Za-z0-9._]+$""".r
    )
  }

  def validatePassword(password: String): ValidationResult = {
    validate(
      Option(password),
      "Password must be minimum 8 characters at least 1 alphabet, 1 numeirc and 1 special characters.",
      """^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9])[A-Za-z0-9!#$%&@_.]{8,}$""".r
    )
  }

  private def validate(value: Option[String], message: String, regex: Regex): ValidationResult = {
    value match {
      case Some(value) =>
        ValidationResult.validate(regex.findFirstMatchIn(value).isDefined, message)
      case None =>
        ValidationResult.Valid
    }
  }

}
