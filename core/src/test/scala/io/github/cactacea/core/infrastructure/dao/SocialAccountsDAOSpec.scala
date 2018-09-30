package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.DAOSpec

class SocialAccountsDAOSpec extends DAOSpec {

  test("create find exist delete") {

    // create
    val sessionAccount1 = createAccount("SocialAccountsDAOSpec1")
    val sessionAccount2 = createAccount("SocialAccountsDAOSpec2")
    val sessionAccount3 = createAccount("SocialAccountsDAOSpec3")
    Await.result(socialAccountsDAO.create("facebook", "SocialAccountsDAOSpec1", None, true, sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.create("google", "SocialAccountsDAOSpec2", None, true, sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.create("twitter", "SocialAccountsDAOSpec3", None, true, sessionAccount3.id.toSessionId))

    // exist
    val exists4 = Await.result(socialAccountsDAO.exist("facebook", sessionAccount3.id.toSessionId))
    val exists5 = Await.result(socialAccountsDAO.exist("google", sessionAccount3.id.toSessionId))
    val exists6 = Await.result(socialAccountsDAO.exist("twitter", sessionAccount3.id.toSessionId))
    val exists8 = Await.result(socialAccountsDAO.exist("facebook", sessionAccount1.id.toSessionId))
    val exists9 = Await.result(socialAccountsDAO.exist("google", sessionAccount1.id.toSessionId))
    val exists10 = Await.result(socialAccountsDAO.exist("twitter", sessionAccount1.id.toSessionId))
    assert(exists4 == true)
    assert(exists5 == true)
    assert(exists6 == true)
    assert(exists8 == false)
    assert(exists9 == false)
    assert(exists10 == false)

    // find
    val list = Await.result(socialAccountsDAO.findAll(sessionAccount3.id.toSessionId))
    assert(list.size == 3)
    val facebook = list(0)
    val google = list(1)
    val twitter = list(2)
    assert(facebook.providerId == "facebook")
    assert(google.providerId == "google")
    assert(twitter.providerId == "twitter")
    assert(facebook.providerKey == "SocialAccountsDAOSpec1")
    assert(google.providerKey == "SocialAccountsDAOSpec2")
    assert(twitter.providerKey == "SocialAccountsDAOSpec3")

    val result6 = Await.result(socialAccountsDAO.find("facebook", "SocialAccountsDAOSpec1"))
    assert(result6.isDefined == true)

    // delete
    Await.result(socialAccountsDAO.delete("facebook", sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.delete("google", sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.delete("twitter", sessionAccount3.id.toSessionId))
    val d1 = Await.result(socialAccountsDAO.exist("facebook", sessionAccount3.id.toSessionId))
    val d2 = Await.result(socialAccountsDAO.exist("google", sessionAccount3.id.toSessionId))
    val d3 = Await.result(socialAccountsDAO.exist("twitter", sessionAccount3.id.toSessionId))
    assert(d1 == false)
    assert(d2 == false)
    assert(d3 == false)

    val result7 = Await.result(socialAccountsDAO.find("facebook", "SocialAccountsDAOSpec1"))
    assert(result7.isDefined == false)

  }

}

