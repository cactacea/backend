package io.github.cactacea.filhouette.impl.providers

import com.twitter.finagle.http.{Fields, Request}
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.crypto.Base64
import io.github.cactacea.filhouette.api.exceptions.ConfigurationException
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.RequestExtractor._
import io.github.cactacea.filhouette.api.util.{Credentials, PasswordHasherRegistry}
import io.github.cactacea.filhouette.api.{LoginInfo, RequestProvider}
import io.github.cactacea.filhouette.impl.providers.BasicAuthProvider._

/**
  * A request provider implementation which supports HTTP basic authentication.
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
class BasicAuthProvider (
                                    protected val authInfoRepository: AuthInfoRepository,
                                    protected val passwordHasherRegistry: PasswordHasherRegistry)
  extends RequestProvider with PasswordProvider {

  /**
    * Gets the provider ID.
    *
    * @return The provider ID.
    */
  override def id: String = ID

  /**
    * Authenticates an identity based on credentials sent in a request.
    *
    * @param request The request.
    * @return Some login info on successful authentication or None if the authentication was unsuccessful.
    */
  override def authenticate(request: Request): Future[Option[LoginInfo]] = {
    getCredentials(request) match {
      case Some(credentials) =>
        val loginInfo = LoginInfo(id, credentials.identifier)
        authenticate(loginInfo, credentials.password).flatMap {
          case Authenticated => Future.value(Some(loginInfo))
          case InvalidPassword(_) =>
            Future.None
          case UnsupportedHasher(error) => Future.exception(new ConfigurationException(error))
          case NotFound(_) =>
            Future.None
        }
      case None => Future.None
    }
  }

  /**
    * Encodes the credentials.
    *
    * @param request Contains the colon-separated name-value pairs in clear-text string format
    * @return The users credentials as plaintext
    */
  def getCredentials(request: Request): Option[Credentials] = {
    request.extractString(Fields.Authorization) match {
      case Some(header) if header.startsWith("Basic ") =>
        Base64.decode(header.replace("Basic ", "")).split(":", 2) match {
          case credentials if credentials.length == 2 => Some(Credentials(credentials(0), credentials(1)))
          case _                                      => None
        }
      case _ => None
    }
  }
}

/**
  * The companion object.
  */
object BasicAuthProvider {

  /**
    * The provider constants.
    */
  val ID = "basic-auth"
}
