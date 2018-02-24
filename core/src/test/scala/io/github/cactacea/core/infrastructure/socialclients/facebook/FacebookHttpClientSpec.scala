package io.github.cactacea.core.util.clients.facebook

import com.twitter.util.Await
import io.github.cactacea.core.helpers.CactaceaDAOTest

class FacebookHttpClientSpec extends CactaceaDAOTest {

  var facebookHttpClient = injector.instance[FacebookHttpClient]

//  test("verify valid token") {
//    val result = Await.result(facebookAPI.find("your access token", ""))
//    assert(result.isDefined == true)
//  }

  test("verify invalid token") {
    val result = Await.result(facebookHttpClient.get("", ""))
    assert(result.isEmpty == true)
  }

}
