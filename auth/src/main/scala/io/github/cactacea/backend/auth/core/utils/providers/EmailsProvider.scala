package io.github.cactacea.backend.auth.core.utils.providers

import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.PasswordHasherRegistry
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

/**
  * A provider for authenticating with credentials.
  *
  * The provider supports the change of password hashing algorithms on the fly. Sometimes it may be possible to change
  * the hashing algorithm used by the application. But the hashes stored in the backing store can't be converted back
  * into plain text passwords, to hash them again with the new algorithm. So if a user successfully authenticates after
  * the application has changed the hashing algorithm, the provider hashes the entered password again with the new
  * algorithm and stores the auth info in the backing store.
  *
  * @param authInfoRepository The auth info repository.
  * @param passwordHasherRegistry The password hashers used by the application.
  */
class EmailsProvider (
                       override protected val authInfoRepository: AuthInfoRepository,
                       override protected val passwordHasherRegistry: PasswordHasherRegistry)
  extends CredentialsProvider(authInfoRepository, passwordHasherRegistry)


/**
  * The companion object.
  */
object EmailsProvider {

  /**
    * The provider constants.
    */
  val ID = "emails"
}


//import com.twitter.util.Future
//import io.github.cactacea.filhouette.api.LoginInfo
//import io.github.cactacea.filhouette.api.exceptions.ConfigurationException
//import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
//import io.github.cactacea.filhouette.api.util.{Credentials, PasswordHasherRegistry}
//import io.github.cactacea.filhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
//import io.github.cactacea.filhouette.impl.providers.CredentialsProvider._
//import io.github.cactacea.filhouette.impl.providers.PasswordProvider
//
///**
//  * A provider for authenticating with emails.
//  *
//  * The provider supports the change of password hashing algorithms on the fly. Sometimes it may be possible to change
//  * the hashing algorithm used by the application. But the hashes stored in the backing store can't be converted back
//  * into plain text passwords, to hash them again with the new algorithm. So if a user successfully authenticates after
//  * the application has changed the hashing algorithm, the provider hashes the entered password again with the new
//  * algorithm and stores the auth info in the backing store.
//  *
//  * @param authInfoRepository The auth info repository.
//  * @param passwordHasherRegistry The password hashers used by the application.
//  */
//class EmailsProvider (
//                                      protected val authInfoRepository: AuthInfoRepository,
//                                      protected val passwordHasherRegistry: PasswordHasherRegistry)
//  extends PasswordProvider {
//
//  /**
//    * Gets the provider ID.
//    *
//    * @return The provider ID.
//    */
//  override def id: String = ID
//
//  /**
//    * Authenticates a user with its credentials.
//    *
//    * @param credentials The credentials to authenticate with.
//    * @return The login info if the authentication was successful, otherwise a failure.
//    */
//  def authenticate(credentials: Credentials): Future[LoginInfo] = {
//    loginInfo(credentials).flatMap { loginInfo =>
//      authenticate(loginInfo, credentials.password).flatMap {
//        case Authenticated            => Future.value(loginInfo)
//        case InvalidPassword(error)   => Future.exception(new InvalidPasswordException(error))
//        case UnsupportedHasher(error) => Future.exception(new ConfigurationException(error))
//        case NotFound(error)          => Future.exception(new IdentityNotFoundException(error))
//      }
//    }
//  }
//
//  /**
//    * Gets the login info for the given credentials.
//    *
//    * Override this method to manipulate the creation of the login info from the emails.
//    *
//    * By default the credentials provider creates the login info with the identifier entered
//    * in the form. For some cases this may not be enough. It could also be possible that a login
//    * form allows a user to log in with either a username or an email address. In this case
//    * this method should be overridden to provide a unique binding, like the user ID, for the
//    * entered form values.
//    *
//    * @param credentials The credentials to authenticate with.
//    * @return The login info created from the credentials.
//    */
//  def loginInfo(credentials: Credentials): Future[LoginInfo] = Future.value(LoginInfo(id, credentials.identifier))
//}
//
//
///**
//  * The companion object.
//  */
//object EmailsProvider {
//
//  /**
//    * The provider constants.
//    */
//  val ID = "emails"
//}
