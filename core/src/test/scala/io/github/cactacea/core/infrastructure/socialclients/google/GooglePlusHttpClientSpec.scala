package io.github.cactacea.core.infrastructure.clients.google

import com.twitter.util.Await
import io.github.cactacea.core.specs.DAOSpec

class GooglePlusHttpClientSpec extends DAOSpec {

  var googlePlusClient = injector.instance[GooglePlusHttpClient]

//  test("verify valid token") {
//    val result = Await.result(googlePlusAPI.find("your access token", ""))
//    assert(result.isDefined == true)
//  }

  test("verify invalid token") {
    val result = Await.result(googlePlusClient.get("", ""))
    assert(result.isEmpty == true)
  }

}
