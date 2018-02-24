package io.github.cactacea.core.helpers

import com.twitter.util.Await
import io.github.cactacea.core.domain.repositories.SessionRepository

class SessionRepositoryTest extends CactaceaDAOTest {


  def signUp(accountName: String, password: String, udid: String) = {

    val sessionRepository = injector.instance[SessionRepository]
    Await.result(sessionRepository.signUp(accountName, accountName, password, udid,
      Some("test@example.com"),
      None,
      Some("location"),
      Some("bio"),
      "user agent"))
    val authentication = Await.result(sessionRepository.signIn(accountName, password, udid, "user agent"))
    authentication
  }

  def signIn(displayName: String, password: String, udid: String) = {

    val sessionRepository = injector.instance[SessionRepository]
    val result = Await.result(sessionRepository.signIn(displayName, password, udid, "user agent"))
    val authentication = Await.result(sessionRepository.signIn(result.account.displayName, password, udid, "user agent"))
    authentication

  }
}
