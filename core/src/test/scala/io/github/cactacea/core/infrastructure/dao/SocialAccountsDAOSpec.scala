package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.specs.DAOSpec

class SocialAccountsDAOSpec extends DAOSpec {

  val socialAccountsDAO: SocialAccountsDAO = injector.instance[SocialAccountsDAO]

  test("create find exist delete") {

    // create
    val sessionAccount1 = this.createAccount(0L)
    val sessionAccount2 = this.createAccount(1L)
    val sessionAccount3 = this.createAccount(2L)
    Await.result(socialAccountsDAO.create(SocialAccountType.facebook, "facebook", sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.create(SocialAccountType.google, "google", sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.create(SocialAccountType.twitter, "twitter", sessionAccount3.id.toSessionId))

    // exist
    val exists4 = Await.result(socialAccountsDAO.exist(SocialAccountType.facebook, sessionAccount3.id.toSessionId))
    val exists5 = Await.result(socialAccountsDAO.exist(SocialAccountType.google, sessionAccount3.id.toSessionId))
    val exists6 = Await.result(socialAccountsDAO.exist(SocialAccountType.twitter, sessionAccount3.id.toSessionId))
    val exists8 = Await.result(socialAccountsDAO.exist(SocialAccountType.facebook, sessionAccount1.id.toSessionId))
    val exists9 = Await.result(socialAccountsDAO.exist(SocialAccountType.google, sessionAccount1.id.toSessionId))
    val exists10 = Await.result(socialAccountsDAO.exist(SocialAccountType.twitter, sessionAccount1.id.toSessionId))
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
    assert(facebook.socialAccountType == SocialAccountType.facebook.toValue)
    assert(google.socialAccountType == SocialAccountType.google.toValue)
    assert(twitter.socialAccountType == SocialAccountType.twitter.toValue)
    assert(facebook.socialAccountId == "facebook")
    assert(google.socialAccountId == "google")
    assert(twitter.socialAccountId == "twitter")

    val result6 = Await.result(socialAccountsDAO.find(SocialAccountType.facebook, "facebook"))
    assert(result6.isDefined == true)

    // delete
    Await.result(socialAccountsDAO.delete(SocialAccountType.facebook, sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.delete(SocialAccountType.google, sessionAccount3.id.toSessionId))
    Await.result(socialAccountsDAO.delete(SocialAccountType.twitter, sessionAccount3.id.toSessionId))
    val d1 = Await.result(socialAccountsDAO.exist(SocialAccountType.facebook, sessionAccount3.id.toSessionId))
    val d2 = Await.result(socialAccountsDAO.exist(SocialAccountType.google, sessionAccount3.id.toSessionId))
    val d3 = Await.result(socialAccountsDAO.exist(SocialAccountType.twitter, sessionAccount3.id.toSessionId))
    assert(d1 == false)
    assert(d2 == false)
    assert(d3 == false)

    val result7 = Await.result(socialAccountsDAO.find(SocialAccountType.facebook, "facebook"))
    assert(result7.isDefined == false)

  }

}

