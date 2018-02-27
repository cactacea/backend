package io.github.cactacea.core.infrastructure.clients.twitter

import com.twitter.util.Await
import io.github.cactacea.core.specs.DAOSpec

class TwitterHttpClientSpec extends DAOSpec {

  var twitterAPI = injector.instance[TwitterHttpClient]

//  test("verify valid token") {
//    val result = Await.result(twitterAPI.find("your access token", "your access token secret"))
//    assert(result.isDefined == true)
//  }

  test("verify invalid token") {
    val result = Await.result(twitterAPI.get("", ""))
    assert(result.isEmpty == true)
  }


}
