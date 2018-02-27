package io.github.cactacea.core.util

import java.util.UUID

import com.twitter.util.{Await, Future}
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.ServerSpec

import scala.util.parsing.json.JSON

class CactaceaServerSpec extends ServerSpec {

  test("sessions test") {

    val f = Future.collect((1 to 1000).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignUp
        val signUpResponse = signUp(s"account$i", s"display$i", s"Password_$i", uuid)
        assert(signUpResponse.statusCode == 200)

        val jsonObject = JSON.parseFull(signUpResponse.contentString)
        jsonObject match {
          case Some(e:Map[String, String]) =>

            val accessToken = e("access_token")

            // PostFeed
            val postFeedResponse = postFeed("Hello, welcome to Cactacea framework site.", List(s"tag$i", s"tag$i"),FeedPrivacyType.everyone, accessToken)
            assert(postFeedResponse.statusCode == 201)

            // SignOut
            val response2 = signOut(accessToken)
            assert(response2.statusCode == 204)

            // SignIn
            val signInResponse = signIn(s"account$i", s"Password_$i", uuid)
            assert(signInResponse.statusCode == 200)

        }

      }
    })

    Await.result(f)

  }



}
