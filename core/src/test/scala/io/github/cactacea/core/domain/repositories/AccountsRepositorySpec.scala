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

    val AccountsRepositorySpec6 = signUp("AccountsRepositorySpec01", "password1", "udid")
    val AccountsRepositorySpec2 = signUp("AccountsRepositorySpec02", "password1", "udid")
    val AccountsRepositorySpec3 = signUp("AccountsRepositorySpec03", "password1", "udid")
    val AccountsRepositorySpec4 = signUp("AccountsRepositorySpec04", "password1", "udid")

    val session = signUp("AccountsRepositorySpec5", "password", "udid")
    val account1 = signUp("AccountsRepositorySpec6", "password1", "udid")
    val blockedAccount1 = signUp("AccountsRepositorySpec7", "password1", "udid")
    val account2 = signUp("AccountsRepositorySpec8", "password2", "udid")
    val blockedAccount2 = signUp("AccountsRepositorySpec9", "password1", "udid")
    val account3 = signUp("AccountsRepositorySpec10", "password3", "udid")
    val blockedAccount3 = signUp("AccountsRepositorySpec11", "password1", "udid")
    val account4 = signUp("AccountsRepositorySpec12", "password4", "udid")
    val blockedAccount4 = signUp("AccountsRepositorySpec13", "password1", "udid")
    val account5 = signUp("AccountsRepositorySpec14", "password5", "udid")
    val blockedAccount5 = signUp("AccountsRepositorySpec15", "password1", "udid")

    execute(blocksRepository.create(blockedAccount1.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount2.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount3.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount4.id, session.id.toSessionId))
    execute(blocksRepository.create(blockedAccount5.id, session.id.toSessionId))

    // Find All
    val accounts1 = execute(accountsRepository.findAll(None, None, 0, 3, session.id.toSessionId))
    assert(accounts1.size == 3)
    assert(accounts1(0).displayName == account5.displayName)
    assert(accounts1(1).displayName == account4.displayName)
    assert(accounts1(2).displayName == account3.displayName)

    val accounts2 = execute(accountsRepository.findAll(None, Some(accounts1(2).next), 0, 3, session.id.toSessionId))
    assert(accounts2.size == 3)
    assert(accounts2(0).displayName == account2.displayName)
    assert(accounts2(1).displayName == account1.displayName)

    // Find By account name
    val accounts3 = execute(accountsRepository.findAll(Some("AccountsRepositorySpec0"), None, 0, 3, session.id.toSessionId))
    assert(accounts3.size == 3)
    assert(accounts3(0).displayName == AccountsRepositorySpec4.displayName)
    assert(accounts3(1).displayName == AccountsRepositorySpec3.displayName)
    assert(accounts3(2).displayName == AccountsRepositorySpec2.displayName)

    val accounts4 = execute(accountsRepository.findAll(Some("AccountsRepositorySpec0"), Some(accounts3(2).next), 0, 3, session.id.toSessionId))
    assert(accounts4.size == 1)
    assert(accounts4(0).displayName == AccountsRepositorySpec6.displayName)

  }

  test("find a account") {

    val session = signUp("AccountsRepositorySpec16", "password", "udid")
    val account = signUp("AccountsRepositorySpec17", "password", "udid")
    val blockingUser = signUp("AccountsRepositorySpec18", "password", "udid")

    execute(blocksRepository.create(session.id, blockingUser.id.toSessionId))

    val result1 = execute(accountsRepository.find(account.id, session.id.toSessionId))
    assert(result1.id == account.id)

    assert(intercept[CactaceaException] {
      execute(accountsRepository.find(blockingUser.id, session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find no exist account") {

    val session = signUp("AccountsRepositorySpec19", "password", "udid")

    assert(intercept[CactaceaException] {
      execute(accountsRepository.find(AccountId(0L), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("find session") {

    val session = signUp("AccountsRepositorySpec20", "password", "udid")

    val result = execute(accountsRepository.find(session.id.toSessionId))
    assert(result.id == session.id)

  }

  test("find no exist session") {

    assert(intercept[CactaceaException] {
      execute(accountsRepository.find(AccountId(0L).toSessionId))
    }.error == AccountNotFound)

  }

  test("update session name") {

    val session = signUp("AccountsRepositorySpec21", "password", "udid")

    execute(accountsRepository.updateAccountName("new account name", session.id.toSessionId))
    val result = execute(accountsRepository.find(session.id.toSessionId))
    assert(result.id == session.id)
    assert(result.accountName == "new account name")

  }

  test("update other account's name") {

    val session = signUp("AccountsRepositorySpec22", "password", "udid")
    val account = signUp("account", "password", "udid")

    execute(accountsRepository.updateDisplayName(account.id, Some("new account name"), session.id.toSessionId))
    val result = execute(accountsRepository.find(account.id, session.id.toSessionId))
    assert(result.id == account.id)
    assert(result.displayName == "new account name")

  }

  test("update profile image with exist medium") {

    val session = signUp("AccountsRepositorySpec23", "session password", "udid")

    val key = "key"
    val uri = "http://cactacea.io/test.jpeg"
    val id = execute(mediumRepository.create(key, uri, Some(uri), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    execute(accountsRepository.updateProfileImage(Some(id), session.id.toSessionId))
    // TODO : Check

  }

  test("delete profile image") {

    val session = signUp("AccountsRepositorySpec24", "session password", "udid")
    execute(accountsRepository.updateProfileImage(None, session.id.toSessionId))
    // TODO : Check

  }

  test("update profile image with no exist medium") {

    val session = signUp("AccountsRepositorySpec25", "session password", "udid")

    assert(intercept[CactaceaException] {
      execute(accountsRepository.updateProfileImage(Some(MediumId(0L)), session.id.toSessionId))
    }.error == MediumNotFound)

  }

  test("update no exist other account'name") {

    val session = signUp("AccountsRepositorySpec26", "password", "udid")

    assert(intercept[CactaceaException] {
      execute(accountsRepository.updateDisplayName(AccountId(0L), Some("new account name"), session.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("exist account name") {

    signUp("AccountsRepositorySpec27", "password", "udid")

    val notExistAccountNameResult = execute(accountsRepository.notExist("AccountsRepositorySpec5 2"))
    assert(notExistAccountNameResult == true)

    val notExistAccountNameResult2 = execute(accountsRepository.notExist("AccountsRepositorySpec5"))
    assert(notExistAccountNameResult2 == false)

  }

  test("update password") {

    val session = signUp("AccountsRepositorySpec28", "password", "udid")
    execute(accountsRepository.updatePassword("password", "new password", session.id.toSessionId))

    val session2 = signIn("AccountsRepositorySpec28", "new password", "udid")
    assert(session2.id == session.id)
    assert(session2.accountName == "AccountsRepositorySpec28")

  }


}
