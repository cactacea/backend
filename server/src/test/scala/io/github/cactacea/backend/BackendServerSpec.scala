package io.github.cactacea.backend

import java.io.PrintWriter
import java.util.UUID

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.helpers.ServerSpec
import io.github.cactacea.backend.models.requests.account.GetAccounts
import io.github.cactacea.backend.models.requests.sessions.{GetSignIn, PostSignUp}
import io.github.cactacea.backend.models.responses.Authentication

@Singleton
class BackendServerSpec @Inject()(mapper: FinatraObjectMapper) extends ServerSpec {

//  @Inject private var mapper: FinatraObjectMapper = _

  val accountsCount = 1

  test("swagger") {

    val swagger = get(s"/docs/model")
    assert(swagger.contentString != "")

    val path = CactaceaBuildInfo.baseDirectory.getParent + "/docs/swagger.json"
    val file = new PrintWriter(path)
    file.write(swagger.contentString)
    file.close()

  }

  test("Sign up") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignUp
        val postSignUp = PostSignUp(s"BackendServerSpec$i", Some(s"BackendServerSpec$i"), s"Backend_Server_Spec_99$i", uuid, None, None, None, None, Some("ios"))
        val body = mapper.writePrettyString(postSignUp)
        val signUpResponse = post("/sessions", body)
        assert(signUpResponse.statusCode == Status.Ok.code)

        val signUpAuth = mapper.parse[Authentication](signUpResponse.contentString)

        assert(delete("/session", signUpAuth.accessToken).statusCode == Status.Ok.code)

      }
    })

    Await.result(f)

  }

  test("Sign in") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignIn
        val getSignIn = GetSignIn(s"BackendServerSpec$i", s"Backend_Server_Spec_99$i", uuid, Some("ios"))
        val signInRes = get(s"/sessions?accountName=${getSignIn.accountName}&password=${getSignIn.password}&udid=${getSignIn.udid}")
        val signInAuth = mapper.parse[Authentication](signInRes.getContentString())
        assert(signInRes.statusCode == Status.Ok.code)

        // SignOut
        assert(delete("/session", signInAuth.accessToken).statusCode == Status.Ok.code)

      }
    })

    Await.result(f)

  }

  test("Follows") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignIn
        val getSignIn = GetSignIn(s"BackendServerSpec$i", s"Backend_Server_Spec_99$i", uuid, Some("ios"))
        val signInRes = get(s"/sessions?accountName=${getSignIn.accountName}&password=${getSignIn.password}&udid=${getSignIn.udid}")
        val signInAuth = mapper.parse[Authentication](signInRes.getContentString())
        assert(signInRes.statusCode == Status.Ok.code)

        // Get Accounts
        val a = GetAccounts(Some("BackendServerSpec"), None, None, None)
        val b = get(s"/accounts${a.displayName.map(name => "?displayName=" + name).getOrElse("")}", signInAuth.accessToken)
        val accounts = mapper.parse[List[Account]](b.getContentString())
        assert(accounts.size == (accountsCount - 1))

        // Follow Accounts
        accounts.foreach({ a =>
          val c = post("/accounts/" + a.id + "/follows", "", signInAuth.accessToken)
          assert(c.statusCode == Status.Ok.code)
        })

        // Get Follow
        val f = get(s"/session/follows", signInAuth.accessToken)
        val g = mapper.parse[List[Account]](f.getContentString())
        assert(g.size == (accountsCount - 1))

      }
    })

    Await.result(f)

  }

  test("Follow") {

    val f = Future.collect((1 to accountsCount).map { i =>
      Future {

        val uuid = UUID.randomUUID().toString

        // SignIn
        val getSignIn = GetSignIn(s"BackendServerSpec$i", s"Backend_Server_Spec_99$i", uuid, Some("ios"))
        val signInRes = get(s"/sessions?accountName=${getSignIn.accountName}&password=${getSignIn.password}&udid=${getSignIn.udid}")
        val signInAuth = mapper.parse[Authentication](signInRes.getContentString())
        assert(signInRes.statusCode == Status.Ok.code)

        // Get Follow
        val f = get(s"/session/follows", signInAuth.accessToken)
        val g = mapper.parse[List[Account]](f.getContentString())
        assert(g.size == (accountsCount - 1))

      }
    })

    Await.result(f)

  }


}
