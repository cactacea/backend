package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.models.Accounts
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountTerminated, InvalidAccountNameOrPassword, SessionTimeout}

class SessionsRepositorySpec extends RepositorySpec {

  val devicesDAO = injector.instance[DevicesDAO]
  var notificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val accountsDAO = injector.instance[AccountsDAO]
  val timeService = injector.instance[TimeService]
  import db._

  test("signUp") {

    val accountName = "SessionsRepositorySpec1"
    val displayName = "SessionsRepositorySpec1"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = Some("userAgent")
    val account = execute(sessionsRepository.signUp(accountName, password, udid,  DeviceType.ios, userAgent))

    // result user
    assert(account.accountName == accountName)
    assert(account.displayName == displayName)

    // result device
    val devices = execute(devicesDAO.exist(account.id.toSessionId, udid))
    assert(devices == true)

    // result notificationSettings
    val notificationSettings = execute(notificationSettingsDAO.find(account.id.toSessionId))
    assert(notificationSettings.isDefined == true)

    // result users
    val users = execute(accountsDAO.find(account.id.toSessionId))
    assert(users.isDefined == true)

  }

  test("signIn") {

    val accountName = "SessionsRepositorySpec2"
    val displayName = "SessionsRepositorySpec2"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = Some("userAgent")
    val result = execute(sessionsRepository.signUp(accountName, password, udid,  DeviceType.ios, userAgent))
    val account = execute(sessionsRepository.signIn(result.accountName, password, udid,  DeviceType.ios, userAgent))

    assert(account.displayName == displayName)

  }

  test("invalid password signIn ") {

    val accountName = "SessionsRepositorySpec3"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = Some("userAgent")

    execute(sessionsRepository.signUp(accountName, password, udid,  DeviceType.ios, userAgent))

    assert(intercept[CactaceaException] {
      execute(sessionsRepository.signIn(accountName, "invalid password", udid,  DeviceType.ios, userAgent))
    }.error == InvalidAccountNameOrPassword)

  }

  test("signOut") {
    val accountName = "SessionsRepositorySpec4"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = Some("userAgent")
    val session = execute(sessionsRepository.signUp(accountName, password, udid,  DeviceType.ios, userAgent))
    execute(sessionsRepository.signOut(udid, session.id.toSessionId))

  }

  test("checkAccountStatus") {

    val accountName = "SessionsRepositorySpec5"
    val password = "password"
    val udid = "0123456789012345678901234567890123456789"
    val userAgent = Some("userAgent")
    val session = execute(sessionsRepository.signUp(accountName, password, udid,  DeviceType.ios, userAgent))

    val expired = timeService.currentTimeMillis()
    execute(sessionsRepository.checkAccountStatus(session.id.toSessionId, expired))

    // Session Timeout
    execute(sessionsRepository.signOut(udid, session.id.toSessionId))
    assert(intercept[CactaceaException] {
      execute(sessionsRepository.checkAccountStatus(session.id.toSessionId, expired))
    }.error == SessionTimeout)

    // Terminated user
    val terminated = AccountStatusType.terminated
    val sessionId = session.id
    val q1 = quote {
      query[Accounts]
        .filter(_.id == lift(sessionId))
        .update(_.accountStatus -> lift(terminated))
    }
    execute(db.run(q1))

    assert(intercept[CactaceaException] {
      execute(sessionsRepository.checkAccountStatus(session.id.toSessionId, expired))
    }.error == AccountTerminated)

  }

}
