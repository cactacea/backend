package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyFriend, AccountNotFound, AccountNotFriend, CanNotSpecifyMyself}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class FriendsRepositorySpec extends RepositorySpec {

  val friendsRepository = injector.instance[FriendsRepository]
  val blocksRepository = injector.instance[BlocksRepository]
  val accountsRepository = injector.instance[AccountsRepository]

  test("create friendship a user") {

    // TODO : Block user
    val sessionUser = signUp("FriendsRepositorySpec1", "session user password", "session user udid")
    val friendUser = signUp("FriendsRepositorySpec2", "friend user password", "friend user udid")
    Await.result(friendsRepository.create(friendUser.id, sessionUser.id.toSessionId))

    val result = Await.result(friendsRepository.findAll(None, None, Some(2), sessionUser.id.toSessionId))
    assert(result.size == 1)
    val resultFollowedUser = result(0)
    assert(friendUser.id == resultFollowedUser.id)

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.id == sessionUser.id)
    assert(account1.followerCount == Some(1L))
    assert(account1.followCount == Some(1L))
    assert(account1.friendCount == Some(1L))

    val account2 = Await.result(accountsRepository.find(friendUser.id.toSessionId))
    assert(account2.id == friendUser.id)
    assert(account2.followerCount == Some(1L))
    assert(account2.followerCount == Some(1L))
    assert(account2.friendCount == Some(1L))

  }

  test("create friendship a blocked user") {

    val sessionUser = signUp("FriendsRepositorySpec3", "session user password", "session user udid")
    val blockingUser = signUp("FriendsRepositorySpec4", "blocked user password", "blocked user udid")

    Await.result(blocksRepository.create(sessionUser.id, blockingUser.id.toSessionId))
    val result = try {
      Await.result(friendsRepository.create(sessionUser.id, blockingUser.id.toSessionId))
      false
    } catch {
      case e: CactaceaException => {
        e.error == AccountNotFound
      }
    }
    assert(result == true)

  }

  test("create friendship a friend user") {

    val sessionUser = signUp("FriendsRepositorySpec5", "session user password", "session user udid")
    val friendUser = signUp("FriendsRepositorySpec6", "friend user password", "friend user udid")

    Await.result(friendsRepository.create(friendUser.id, sessionUser.id.toSessionId))

    val result = try {
      Await.result(friendsRepository.create(friendUser.id, sessionUser.id.toSessionId))
      false
    } catch {
      case e: CactaceaException => {
        e.error == AccountAlreadyFriend
      }
    }
    assert(result == true)

  }

  test("create friend a session user") {

    val sessionUser = signUp("FriendsRepositorySpec7", "session user password", "session user udid")

    val result = try {
      Await.result(friendsRepository.create(sessionUser.id, sessionUser.id.toSessionId))
      false
    } catch {
      case e: CactaceaException => {
        e.error == CanNotSpecifyMyself
      }
    }
    assert(result == true)

  }





  test("delete friendship") {

    val sessionUser = signUp("FriendsRepositorySpec8", "session user password", "session user udid")
    val friendUser = signUp("FriendsRepositorySpec9", "friend user password", "friend user udid")

    Await.result(friendsRepository.create(friendUser.id, sessionUser.id.toSessionId))

    val account1 = Await.result(accountsRepository.find(sessionUser.id.toSessionId))
    assert(account1.friendCount == Some(1L))
    assert(account1.followerCount == Some(1L))
    assert(account1.followCount == Some(1L))

    Await.result(friendsRepository.delete(friendUser.id, sessionUser.id.toSessionId))
    val account2 = Await.result(accountsRepository.find(friendUser.id.toSessionId))
    assert(account2.followerCount == Some(1L))
    assert(account2.followerCount == Some(1L))
    assert(account2.friendCount == Some(0L))

  }

  test("delete no friend friendship ") {

    val sessionUser = signUp("FriendsRepositorySpec10", "session user password", "session user udid")
    val friendUser = signUp("FriendsRepositorySpec11", "friend user password", "friend user udid")

    val result = try {
      Await.result(friendsRepository.delete(friendUser.id, sessionUser.id.toSessionId))
      false
    } catch {
      case e: CactaceaException => {
        e.error == AccountNotFriend
      }
    }
    assert(result == true)

  }

  test("delete no exist account friendship ") {

    val sessionUser = signUp("FriendsRepositorySpec12", "session user password", "session user udid")

    val result = try {
      Await.result(friendsRepository.delete(AccountId(0L), sessionUser.id.toSessionId))
      false
    } catch {
      case e: CactaceaException => {
        e.error == AccountNotFound
      }
    }
    assert(result == true)

  }

  test("delete friend session user") {

    val sessionUser = signUp("FriendsRepositorySpec13", "session user password", "session user udid")

    val result = try {
      Await.result(friendsRepository.delete(sessionUser.id, sessionUser.id.toSessionId))
      false
    } catch {
      case e: CactaceaException => {
        e.error == CanNotSpecifyMyself
      }
    }
    assert(result == true)

  }




  test("find a user's friends") {

    val sessionUser = signUp("FriendsRepositorySpec14", "session user password", "session user udid")
    val user = signUp("FriendsRepositorySpec15", "user password", "user udid")
    val friendUser1 = signUp("FriendsRepositorySpec16", "user password 1", "user udid 1")
    val friendUser2 = signUp("FriendsRepositorySpec17", "user password 2", "user udid 2")
    val friendUser3 = signUp("FriendsRepositorySpec18", "user password 3", "user udid 3")
    val friendUser4 = signUp("FriendsRepositorySpec19", "user password 4", "user udid 4")
    val friendUser5 = signUp("FriendsRepositorySpec20", "user password 5", "user udid 5")

    Await.result(friendsRepository.create(friendUser1.id, user.id.toSessionId))
    Await.result(friendsRepository.create(friendUser2.id, user.id.toSessionId))
    Await.result(friendsRepository.create(friendUser3.id, user.id.toSessionId))
    Await.result(friendsRepository.create(friendUser4.id, user.id.toSessionId))
    Await.result(friendsRepository.create(friendUser5.id, user.id.toSessionId))

    val friends1 = Await.result(friendsRepository.findAll(user.id, None, None, Some(3), sessionUser.id.toSessionId))
    assert(friends1.size == 3)
    assert(friends1(0).id == friendUser5.id)
    assert(friends1(1).id == friendUser4.id)
    assert(friends1(2).id == friendUser3.id)

    val friends2 = Await.result(friendsRepository.findAll(user.id, Some(friends1(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(friends2.size == 2)
    assert(friends2(0).id == friendUser2.id)
    assert(friends2(1).id == friendUser1.id)

  }

  test("find session's friends") {

    val sessionUser = signUp("FriendsRepositorySpec21", "session user password", "session user udid")
    val friendUser1 = signUp("FriendsRepositorySpec22", "user password 1", "user udid 1")
    val friendUser2 = signUp("FriendsRepositorySpec23", "user password 2", "user udid 2")
    val friendUser3 = signUp("FriendsRepositorySpec24", "user password 3", "user udid 3")
    val friendUser4 = signUp("FriendsRepositorySpec25", "user password 4", "user udid 4")
    val friendUser5 = signUp("FriendsRepositorySpec26", "user password 5", "user udid 5")

    Await.result(friendsRepository.create(friendUser1.id, sessionUser.id.toSessionId))
    Await.result(friendsRepository.create(friendUser2.id, sessionUser.id.toSessionId))
    Await.result(friendsRepository.create(friendUser3.id, sessionUser.id.toSessionId))
    Await.result(friendsRepository.create(friendUser4.id, sessionUser.id.toSessionId))
    Await.result(friendsRepository.create(friendUser5.id, sessionUser.id.toSessionId))

    val friends = Await.result(friendsRepository.findAll(None, None, Some(3), sessionUser.id.toSessionId))
    assert(friends.size == 3)
    assert(friends(0).id == friendUser5.id)
    assert(friends(1).id == friendUser4.id)
    assert(friends(2).id == friendUser3.id)

    val friends2 = Await.result(friendsRepository.findAll(Some(friends(2).next), None, Some(3), sessionUser.id.toSessionId))
    assert(friends2.size == 2)
    assert(friends2(0).id == friendUser2.id)
    assert(friends2(1).id == friendUser1.id)

  }

}
