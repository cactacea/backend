package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.helpers.SessionRepositoryTest
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.models.Accounts
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotFound, AccountTerminated, SessionTimeout}

class SessionRepositorySpec extends SessionRepositoryTest {

  val sessionRepository = injector.instance[SessionRepository]
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
    val authentication = Await.result(sessionRepository.signUp(accountName, displayName, password, udid, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent))

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
    val result = Await.result(sessionRepository.signUp(accountName, displayName, password, udid, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent))
    val authentication = Await.result(sessionRepository.signIn(result.account.displayName, password, udid, userAgent))

    assert(authentication.account.displayName == displayName)
    assert(authentication.accessToken != "")

  }

  test("invalid password signIn ") {

    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"

    Await.result(sessionRepository.signUp(accountName, displayName, password, udid, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent))

    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.signIn(displayName, "invalid password", udid, userAgent))
    }.error == AccountNotFound)

  }

  test("signOut") {
    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"
    val session = Await.result(sessionRepository.signUp(accountName, displayName, password, udid, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent)).account
    val result = Await.result(sessionRepository.signOut(udid, session.id.toSessionId))
    assert(result == true)

  }

  test("checkAccountStatus") {

    val accountName = "new account"
    val displayName = "new account"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = "userAgent"
    val session = Await.result(sessionRepository.signUp(accountName, displayName, password, udid, Some("test@example.com"), None, Some("location"), Some("bio"), userAgent)).account

    val expired = System.nanoTime()
    assert(Await.result(sessionRepository.checkAccountStatus(session.id.toSessionId, expired)) == true)

    // Session Timeout
    Await.result(sessionRepository.signOut(udid, session.id.toSessionId))
    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.checkAccountStatus(session.id.toSessionId, expired))
    }.error == SessionTimeout)

    // Terminated user
    val terminated = AccountStatusType.terminated.toValue
    val sessionId = session.id
    val q1 = quote {
      query[Accounts]
        .filter(_.id == lift(sessionId))
        .update(_.accountStatus -> lift(terminated))
    }
    Await.result(db.run(q1))

    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.checkAccountStatus(session.id.toSessionId, expired))
    }.error == AccountTerminated)

  }

  test("signUp by google") {
    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.signUp(
        SocialAccountType.google,
        "account name",
        "display name",
        "account password",
        "token key",
        "token secret",
        "udid",
        Some("test@example.com"),
        None,
        Some("location"),
        Some("bio"),
        "userAgent"
      ))
    }.error == SessionTimeout)
  }

  test("signUp by facebook") {
    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.signUp(
        SocialAccountType.facebook,
        "account name",
        "display name",
        "account password",
        "token key",
        "token secret",
        "udid",
        Some("test@example.com"),
        None,
        Some("location"),
        Some("bio"),
        "userAgent"
      ))
    }.error == SessionTimeout)

  }

  test("signUp by twitter") {
    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.signUp(
        SocialAccountType.twitter,
        "account name",
        "display name",
        "account password",
        "token key",
        "token secret",
        "udid",
        Some("test@example.com"),
        None,
        Some("location"),
        Some("bio"),
        "userAgent"
      ))
    }.error == SessionTimeout)

  }

  test("signIn by google") {

    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.signIn("token key", "token secret", "udid", "user agent"))
    }.error == AccountNotFound)

  }

  test("signIn by facebook") {

    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.signIn("token key", "token secret", "udid", "user agent"))
    }.error == AccountNotFound)

  }

  test("signIn by twitter") {

    assert(intercept[CactaceaException] {
      Await.result(sessionRepository.signIn("token key", "token secret", "udid", "user agent"))
    }.error == AccountNotFound)

  }


}
