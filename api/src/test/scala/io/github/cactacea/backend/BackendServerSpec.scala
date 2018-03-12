package io.github.cactacea.backend

import java.util.UUID

import com.google.inject.Inject
import com.twitter.finagle.http.Status
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.helpers.ServerSpec
import io.github.cactacea.backend.models.requests.account.GetAccounts
import io.github.cactacea.backend.models.requests.session.{GetSignIn, PostSignUp}
import io.github.cactacea.core.domain.models.{Account, Authentication}

class BackendServerSpec extends ServerSpec {

  @Inject private var mapper: FinatraObjectMapper = _

  val accountsCount = 10

  test("Sign up") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignUp
        val postSignUp = PostSignUp(s"account$i", s"display$i", s"Password_$i", uuid, None, None, None, None, "ios")
        val body = mapper.writePrettyString(postSignUp)
        val signUpResponse = post("/sessions", body)
        assert(signUpResponse.statusCode == Status.Ok.code)

        val signUpAuth = mapper.parse[Authentication](signUpResponse.contentString)

        // SignOut
        val signOutRes = signOut(signUpAuth.accessToken)
        assert(signOutRes.statusCode == Status.NoContent.code)

      }
    })

    Await.result(f)

  }

  test("Sign in") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignIn
        val getSignIn = GetSignIn(s"account$i", s"Password_$i", uuid, "ios")
        val signInRes = get(s"/sessions?account_name=${getSignIn.accountName}&password=${getSignIn.password}&udid=${getSignIn.udid}")
        val signInAuth = mapper.parse[Authentication](signInRes.getContentString())
        assert(signInRes.statusCode == Status.Ok.code)

        // SignOut
        val signOutRes = signOut(signInAuth.accessToken)
        assert(signOutRes.statusCode == Status.NoContent.code)

      }
    })

    Await.result(f)

  }

  test("Follows") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignIn
        val getSignIn = GetSignIn(s"account$i", s"Password_$i", uuid, "ios")
        val signInRes = get(s"/sessions?account_name=${getSignIn.accountName}&password=${getSignIn.password}&udid=${getSignIn.udid}")
        val signInAuth = mapper.parse[Authentication](signInRes.getContentString())
        assert(signInRes.statusCode == Status.Ok.code)

        // Get Accounts
        val a = GetAccounts(Some("account"), None, None, None)
        val b = get(s"/accounts${a.displayName.map(name => "?display_name=" + name).getOrElse("")}", signInAuth.accessToken)
        val accounts = mapper.parse[List[Account]](b.getContentString())
        assert(accounts.size == (accountsCount - 1))

        // Follow Accounts
        accounts.foreach({ a =>
          val c = post("/accounts/" + a.id + "/following", "", signInAuth.accessToken)
          assert(c.statusCode == Status.NoContent.code)
        })

        // Get Following
        val f = get(s"/session/following", signInAuth.accessToken)
        val g = mapper.parse[List[Account]](f.getContentString())
        assert(g.size == (accountsCount - 1))

      }
    })

    Await.result(f)

  }

  test("Following") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignIn
        val getSignIn = GetSignIn(s"account$i", s"Password_$i", uuid, "ios")
        val signInRes = get(s"/sessions?account_name=${getSignIn.accountName}&password=${getSignIn.password}&udid=${getSignIn.udid}")
        val signInAuth = mapper.parse[Authentication](signInRes.getContentString())
        assert(signInRes.statusCode == Status.Ok.code)

        // Get Following
        val f = get(s"/session/following", signInAuth.accessToken)
        val g = mapper.parse[List[Account]](f.getContentString())
        assert(g.size == (accountsCount - 1))

      }
    })

    Await.result(f)

  }


}