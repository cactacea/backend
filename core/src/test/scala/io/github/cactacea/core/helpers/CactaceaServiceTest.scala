package io.github.cactacea.core.helpers

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.util.Await
import io.github.cactacea.core.application.services.SessionService

class CactaceaServiceTest extends CactaceaTest {

  @Inject var sessionService: SessionService = _

  def signUp(displayName: String, password: String, udid: String) = {
    Await.result(sessionService.signUp(displayName, displayName, "account password", "ffc1ded6f4570d557ad65f986684fc10c7f8d51f", Some("test@example.com"), None, Some("location"), Some("bio"), "user-agent"))

  }


}
