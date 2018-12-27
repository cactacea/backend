package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, MediumNotFound}

class AccountsRepositorySpec extends RepositorySpec {

  val accountsRepository = injector.instance[AccountsRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val mediumRepository = injector.instance[MediumsRepository]

  test("find accounts") {


    val session = signUp("aaa_test_account_05", "password", "udid")
    val account1 = signUp("aaa_test_account_06", "password1", "udid")
    val blockedAccount1 = signUp("aaa_test_account_07", "password1", "udid")
    val account2 = signUp("aaa_test_account_08", "password2", "udid")
    val blockedAccount2 = signUp("aaa_test_account_09", "password1", "udid")
    val account3 = signUp("aaa_test_account_10", "password3", "udid")
    val blockedAccount3 = signUp("aaa_test_account_11", "password1", "udid")
    val account4 = signUp("aaa_test_account_12", "password4", "udid")
    val blockedAccount4 = signUp("aaa_test_account_13", "password1", "udid")
    val account5 = signUp("aaa_test_account_14", "password5", "udid")
    val blockedAccount5 = signUp("aaa_test_account_15", "password1", "udid")

    execute(blocksRepository.create(blockedAccount1.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount2.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount3.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount4.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount5.id, session.id.toSessionId))

    // Find All
    val accounts1 = execute(accountsRepository.find(None, None, 0, 3, session.id.toSessionId))
    assert(accounts1.size == 3)
    assert(accounts1(0).accountName ==account1.accountName)
    assert(accounts1(1).accountName ==account2.accountName)
    assert(accounts1(2).accountName ==account3.accountName)

    val accounts2 = execute(accountsRepository.find(None, accounts1(2).next, 0, 3, session.id.toSessionId))
    assert(accounts2.size == 3)
    assert(accounts2(0).accountName ==account4.accountName)
    assert(accounts2(1).accountName ==account5.accountName)

    val AccountsRepositorySpec1 = signUp("bbb_test_account_01", "password1", "udid")
    val AccountsRepositorySpec2 = signUp("bbb_test_account_02", "password1", "udid")
    val AccountsRepositorySpec3 = signUp("bbb_test_account_03", "password1", "udid")
    val AccountsRepositorySpec4 = signUp("bbb_test_account_04", "password1", "udid")

    // Find By account name
    val accounts3 = execute(accountsRepository.find(Some("bbb_test_account_0"), None, 0, 3, session.id.toSessionId))
    assert(accounts3.size == 3)
    assert(accounts3(0).accountName ==AccountsRepositorySpec1.accountName)
    assert(accounts3(1).accountName ==AccountsRepositorySpec2.accountName)
    assert(accounts3(2).accountName ==AccountsRepositorySpec3.accountName)

    val accounts4 = execute(accountsRepository.find(Some("bbb_test_account_0"), accounts3(2).next, 0, 3, session.id.toSessionId))
    assert(accounts4.size == 1)
    assert(accounts4(0).accountName ==AccountsRepositorySpec4.accountName)

  }

  test("find a account") {

    val session = signUp("aaa_test_account_16", "password", "udid")
    val account = signUp("aaa_test_account_17", "password", "udid")
    val blockingUser = signUp("aaa_test_account_18", "password", "udid")

    execute(blocksRepository.create(session.id, blockingUser.id.toSessionId))

    val result1 = execute(accountsRepository.findDetail(account.id, session.id.toSessionId))
    assert(result1.id == account.id)

    assert(intercept[CactaceaException] {
      execute(accountsRepository.findDetail(blockingUser.id, session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find no exist account") {

    val session = signUp("aaa_test_account_19", "password", "udid")

    assert(intercept[CactaceaException] {
      execute(accountsRepository.findDetail(AccountId(0L), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find session") {

    val session = signUp("aaa_test_account_20", "password", "udid")

    val result = execute(accountsRepository.find(session.id.toSessionId))
    assert(result.id == session.id)

  }

  test("find no exist session") {

    assert(intercept[CactaceaException] {
      execute(accountsRepository.find(AccountId(0L).toSessionId))
    }.error == AccountNotFound)

  }

  test("update session name") {

    val session = signUp("aaa_test_account_21", "password", "udid")

    execute(accountsRepository.updateAccountName("new account name", session.id.toSessionId))
    val result = execute(accountsRepository.find(session.id.toSessionId))
    assert(result.id == session.id)
    assert(result.accountName == "new account name")

  }

  test("update other account's name") {

    val session = signUp("aaa_test_account_22", "password", "udid")
    val account = signUp("account", "password", "udid")

    execute(accountsRepository.updateDisplayName(account.id, Some("new account name"), session.id.toSessionId))
    val result = execute(accountsRepository.findDetail(account.id, session.id.toSessionId))
    assert(result.id == account.id)
    assert(result.displayName =="new account name")

  }

  test("update profile image with exist medium") {

    val session = signUp("aaa_test_account_23", "session password", "udid")

    val key = "key"
    val uri = "http://cactacea.io/test.jpeg"
    val id = execute(mediumRepository.create(key, uri, Some(uri), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    execute(accountsRepository.updateProfileImage(Some(id), session.id.toSessionId))
    // TODO : Check

  }

  test("delete profile image") {

    val session = signUp("aaa_test_account_24", "session password", "udid")
    execute(accountsRepository.updateProfileImage(None, session.id.toSessionId))
    // TODO : Check

  }

  test("update profile image with no exist medium") {

    val session = signUp("aaa_test_account_25", "session password", "udid")

    assert(intercept[CactaceaException] {
      execute(accountsRepository.updateProfileImage(Some(MediumId(0L)), session.id.toSessionId))
    }.error == MediumNotFound)

  }

  test("update no exist other account'name") {

    val session = signUp("aaa_test_account_26", "password", "udid")

    assert(intercept[CactaceaException] {
      execute(accountsRepository.updateDisplayName(AccountId(0L), Some("new account name"), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("exist account name") {

    signUp("aaa_test_account_27", "password", "udid")

    val notExistAccountNameResult = execute(accountsRepository.notExist("aaa_test_account_05 2"))
    assert(notExistAccountNameResult == true)

    val notExistAccountNameResult2 = execute(accountsRepository.notExist("aaa_test_account_05"))
    assert(notExistAccountNameResult2 == false)

  }

  test("update password") {

    val session = signUp("aaa_test_account_28", "password", "udid")
    execute(accountsRepository.updatePassword("password", "new password", session.id.toSessionId))

    val session2 = signIn("aaa_test_account_28", "new password", "udid")
    assert(session2.id == session.id)
    assert(session2.accountName == "aaa_test_account_28")

  }


}
