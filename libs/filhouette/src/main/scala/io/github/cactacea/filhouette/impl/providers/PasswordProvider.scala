package io.github.cactacea.filhouette.impl.providers

import com.twitter.util.Future
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.{PasswordHasherRegistry, PasswordInfo}
import io.github.cactacea.filhouette.api.{LoginInfo, Provider}
import io.github.cactacea.filhouette.impl.providers.PasswordProvider._

/**
  * Base provider which provides shared functionality to authenticate with a password.
  */
trait PasswordProvider extends Provider {

  /**
    * The authentication state.
    */
  sealed trait State

  /**
    * Indicates that the authentication was successful.
    */
  case object Authenticated extends State

  /**
    * Indicates that the entered password doesn't match with the stored one.
    */
  case class InvalidPassword(error: String) extends State

  /**
    * Indicates that the stored password cannot be checked with the registered hashers.
    */
  case class UnsupportedHasher(error: String) extends State

  /**
    * Indicates that no password info was stored for the login info.
    */
  case class NotFound(error: String) extends State

  /**
    * The auth info repository.
    */
  protected val authInfoRepository: AuthInfoRepository

  /**
    * The password hashers used by the application.
    */
  protected val passwordHasherRegistry: PasswordHasherRegistry

  /**
    * Authenticates a user
    *
    * @param loginInfo The login info to search the password info for.
    * @param password The user password to authenticate with.
    * @return The authentication state.
    */
  def authenticate(loginInfo: LoginInfo, password: String): Future[State] = {
    authInfoRepository.find[PasswordInfo](loginInfo).flatMap {
      case Some(passwordInfo) => passwordHasherRegistry.find(passwordInfo) match {
        case Some(hasher) if hasher.matches(passwordInfo, password) =>
          if (passwordHasherRegistry.isDeprecated(hasher) || hasher.isDeprecated(passwordInfo).contains(true)) {
            authInfoRepository.update(loginInfo, passwordHasherRegistry.current.hash(password)).map { _ =>
              Authenticated
            }
          } else {
            Future.value(Authenticated)
          }
        case Some(_) => Future.value(InvalidPassword(PasswordDoesNotMatch.format(id)))
        case None => Future.value(UnsupportedHasher(HasherIsNotRegistered.format(
          id, passwordInfo.hasher, passwordHasherRegistry.all.map(_.id).mkString(", ")
        )))
      }
      case None => Future.value(NotFound(PasswordInfoNotFound.format(id, loginInfo)))
    }
  }
}

/**
  * The companion object.
  */
object PasswordProvider {

  /**
    * The error messages.
    */
  val PasswordDoesNotMatch = "[Filhouette][%s] Password does not match"
  val HasherIsNotRegistered = "[Filhouette][%s] Stored hasher ID `%s` isn't registered as supported hasher: %s"
  val PasswordInfoNotFound = "[Filhouette][%s] Could not find password info for given login info: %s"
}
