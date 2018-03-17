package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.models.Accounts
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaErrors.{AccountTerminated, InvalidAccountNameOrPassword, SessionTimeout}

class SessionsRepositorySpec extends RepositorySpec {

  val sessionsRepository = injector.instance[SessionsRepository]
  val devicesDAO = injector.instance[DevicesDAO]
  val socialAccountsDAO = injector.instance[SocialAccountsDAO]
  var advertisementSettingsDAO = injector.instance[AdvertisementSettingsDAO]
  var notificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val accountsDAO = injector.instance[AccountsDAO]

  import db._

  test("signUp") {

    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"
    val authentication = Await.result(sessionsRepository.signUp(accountName, displayName, password, udid,  DeviceType.ios, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent))

    // result user
    assert(authentication.account.accountName == accountName)
    assert(authentication.account.displayName == displayName)

    // result access token
    assert(authentication.accessToken != "")

    // result device
    val devices = Await.result(devicesDAO.exist(authentication.account.id.toSessionId, udid))
    assert(devices == true)

    // result advertisementSettings
    val advertisementSettings = Await.result(advertisementSettingsDAO.find(authentication.account.id.toSessionId))
    assert(advertisementSettings.isDefined == true)

    // result notificationSettings
    val notificationSettings = Await.result(notificationSettingsDAO.find(authentication.account.id.toSessionId))
    assert(notificationSettings.isDefined == true)

    // result users
    val users = Await.result(accountsDAO.find(authentication.account.id.toSessionId))
    assert(users.isDefined == true)

  }

  test("signIn") {

    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"
    val result = Await.result(sessionsRepository.signUp(accountName, displayName, password, udid,  DeviceType.ios, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent))
    val authentication = Await.result(sessionsRepository.signIn(result.account.displayName, password, udid,  DeviceType.ios, userAgent))

    assert(authentication.account.displayName == displayName)
    assert(authentication.accessToken != "")

  }

  test("invalid password signIn ") {

    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"

    Await.result(sessionsRepository.signUp(accountName, displayName, password, udid,  DeviceType.ios, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent))

    assert(intercept[CactaceaException] {
      Await.result(sessionsRepository.signIn(displayName, "invalid password", udid,  DeviceType.ios, userAgent))
    }.error == InvalidAccountNameOrPassword)

  }

  test("signOut") {
    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"
    val session = Await.result(sessionsRepository.signUp(accountName, displayName, password, udid,  DeviceType.ios, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent)).account
    val result = Await.result(sessionsRepository.signOut(udid, session.id.toSessionId))
    assert(result == true)

  }

  test("checkAccountStatus") {

    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"
    val session = Await.result(sessionsRepository.signUp(accountName, displayName, password, udid,  DeviceType.ios, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent)).account

    val expired = System.currentTimeMillis
    assert(Await.result(sessionsRepository.checkAccountStatus(session.id.toSessionId, expired)) == true)

    // Session Timeout
    Await.result(sessionsRepository.signOut(udid, session.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(sessionsRepository.checkAccountStatus(session.id.toSessionId, expired))
    }.error == SessionTimeout)

    // Terminated user
    val terminated = AccountStatusType.terminated
    val sessionId = session.id
    val q1 = quote {
      query[Accounts]
        .filter(_.id == lift(sessionId))
        .update(_.accountStatus -> lift(terminated))
    }
    Await.result(db.run(q1))

    assert(intercept[CactaceaException] {
      Await.result(sessionsRepository.checkAccountStatus(session.id.toSessionId, expired))
    }.error == AccountTerminated)

  }

  test("social account signUp") {
    val result = Await.result(sessionsRepository.signUp(
      "facebook",
      "accountName",
      "displayName",
      "accountPassword",
      "facebook",
      "token key",
      "token secret",
      "udid",
      DeviceType.ios,
      Some("test@example.com"),
      None,
      Some("location"),
      Some("bio"),
      "userAgent"
    ))
    assert(result.account.accountName == "accountName")

  }

  test("social account signIn") {

    assert(intercept[CactaceaException] {
      Await.result(sessionsRepository.signIn("tokenkey", "token secret", "udid", DeviceType.ios, "user agent"))
    }.error == InvalidAccountNameOrPassword)

  }


}
