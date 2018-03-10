package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotFound, MediumNotFound}

class AccountsRepositorySpec extends RepositorySpec {

  val accountsRepository = injector.instance[AccountsRepository]
  val sessionsRepository = injector.instance[SessionsRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val mediumRepository = injector.instance[MediumsRepository]

  test("find accounts") {

    val account61 = signUp("account61", "password1", "udid").account
    val account62 = signUp("account62", "password1", "udid").account
    val account63 = signUp("account63", "password1", "udid").account
    val account64 = signUp("account64", "password1", "udid").account

    val session = signUp("session account", "password", "udid").account
    val account1 = signUp("account1", "password1", "udid").account
    val blockAccount1 = signUp("block account1", "password1", "udid").account
    val account2 = signUp("account2", "password2", "udid").account
    val blockAccount2 = signUp("block account2", "password1", "udid").account
    val account3 = signUp("account3", "password3", "udid").account
    val blockAccount3 = signUp("block account3", "password1", "udid").account
    val account4 = signUp("account4", "password4", "udid").account
    val blockAccount4 = signUp("block account4", "password1", "udid").account
    val account5 = signUp("account5", "password5", "udid").account
    val blockAccount5 = signUp("block account5", "password1", "udid").account

    Await.result(blocksRepository.create(blockAccount1.id, session.id.toSessionId))
    Await.result(blocksRepository.create(blockAccount2.id, session.id.toSessionId))
    Await.result(blocksRepository.create(blockAccount3.id, session.id.toSessionId))
    Await.result(blocksRepository.create(blockAccount4.id, session.id.toSessionId))
    Await.result(blocksRepository.create(blockAccount5.id, session.id.toSessionId))

    // Find All
    val accounts1 = Await.result(accountsRepository.findAll(None, None, None, Some(3), session.id.toSessionId))
    assert(accounts1.size == 3)
    assert(accounts1(0).displayName == account5.displayName)
    assert(accounts1(1).displayName == account4.displayName)
    assert(accounts1(2).displayName == account3.displayName)

    val accounts2 = Await.result(accountsRepository.findAll(None, Some(accounts1(2).next), None, Some(3), session.id.toSessionId))
    assert(accounts2.size == 3)
    assert(accounts2(0).displayName == account2.displayName)
    assert(accounts2(1).displayName == account1.displayName)

    // Find By account name
    val accounts3 = Await.result(accountsRepository.findAll(Some("account6"), None, None, Some(3), session.id.toSessionId))
    assert(accounts3.size == 3)
    assert(accounts3(0).displayName == account64.displayName)
    assert(accounts3(1).displayName == account63.displayName)
    assert(accounts3(2).displayName == account62.displayName)

    val accounts4 = Await.result(accountsRepository.findAll(None, Some(accounts3(2).next), None, Some(3), session.id.toSessionId))
    assert(accounts4.size == 1)
    assert(accounts4(0).displayName == account61.displayName)

  }

  test("find a account") {

    val session = signUp("session account", "password", "udid").account
    val account = signUp("account", "password", "udid").account
    val blockAccount = signUp("block account", "password", "udid").account

    Await.result(blocksRepository.create(blockAccount.id, session.id.toSessionId))

    val result1 = Await.result(accountsRepository.find(account.id, session.id.toSessionId))
    assert(result1.id == account.id)

    assert(intercept[CactaceaException] {
      Await.result(accountsRepository.find(blockAccount.id, session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find no exist account") {

    val session = signUp("session account", "password", "udid").account

    assert(intercept[CactaceaException] {
      Await.result(accountsRepository.find(AccountId(0L), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find session") {

    val session = signUp("session account", "password", "udid").account

    val result = Await.result(accountsRepository.find(session.id.toSessionId))
    assert(result.id == session.id)

  }

  test("find no exist session") {

    assert(intercept[CactaceaException] {
      Await.result(accountsRepository.find(AccountId(0L).toSessionId))
    }.error == AccountNotFound)

  }

  test("update session name") {

    val session = signUp("session account", "password", "udid").account

    Await.result(accountsRepository.updateAccountName("new account name", session.id.toSessionId))
    val result = Await.result(accountsRepository.find(session.id.toSessionId))
    assert(result.id == session.id)
    assert(result.accountName == "new account name")

  }

  test("update no exist session name") {

    assert(intercept[CactaceaException] {
      Await.result(accountsRepository.updateAccountName("new account name", AccountId(0L).toSessionId))
    }.error == AccountNotFound)

  }

  test("update other account's name") {

    val session = signUp("session account", "password", "udid").account
    val account = signUp("account", "password", "udid").account

    Await.result(accountsRepository.updateDisplayName(account.id, Some("new account name"), session.id.toSessionId))
    val result = Await.result(accountsRepository.find(account.id, session.id.toSessionId))
    assert(result.id == account.id)
    assert(result.displayName == "new account name")

  }

  test("update profile image with exist medium") {

    val session = signUp("session name", "session password", "udid").account

    val key = "key"
    val uri = "http://cactacea.io/test.jpeg"
    val (id, url) = Await.result(mediumRepository.create(key, uri, Some(uri), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val result = Await.result(accountsRepository.updateProfileImage(Some(id), session.id.toSessionId))
    // TODO : Check

  }

  test("delete profile image") {

    val session = signUp("session name", "session password", "udid").account
    val result = Await.result(accountsRepository.updateProfileImage(None, session.id.toSessionId))
    // TODO : Check

  }

  test("update profile image with no exist medium") {

    val session = signUp("session name", "session password", "udid").account

    assert(intercept[CactaceaException] {
      Await.result(accountsRepository.updateProfileImage(Some(MediumId(0L)), session.id.toSessionId))
    }.error == MediumNotFound)

  }

  test("update no exist other account'name") {

    val session = signUp("session account", "password", "udid").account

    assert(intercept[CactaceaException] {
      Await.result(accountsRepository.updateDisplayName(AccountId(0L), Some("new account name"), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("exist account name") {

    val session = signUp("session account", "password", "udid").account

    val notExistAccountNameResult = Await.result(accountsRepository.notExist("session account 2"))
    assert(notExistAccountNameResult == true)

    val notExistAccountNameResult2 = Await.result(accountsRepository.notExist("session account"))
    assert(notExistAccountNameResult2 == false)

  }

  test("update password") {

    val session = signUp("session account", "password", "udid").account
    Await.result(accountsRepository.updatePassword("password", "new password", session.id.toSessionId))

    val session2 = this.signIn("session account", "new password", "udid").account
    assert(session2.id == session.id)
    assert(session2.displayName == "session account")

  }

  test("update no exist account password") {

    assert(intercept[CactaceaException] {
      Await.result(accountsRepository.updatePassword("password", "new password", SessionId(0L)))
    }.error == AccountNotFound)

  }

}
