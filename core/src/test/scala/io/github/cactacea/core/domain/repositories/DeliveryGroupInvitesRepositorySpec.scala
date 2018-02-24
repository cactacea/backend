package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.helpers.SessionRepositoryTest
import io.github.cactacea.core.infrastructure.identifiers.GroupInviteId

class DeliveryGroupInvitesRepositorySpec extends SessionRepositoryTest {

  val devicesRepository = injector.instance[DevicesRepository]
  val deliveryGroupInvitesRepository = injector.instance[DeliveryGroupInvitesRepository]
  val groupInvitesRepository = injector.instance[GroupInvitesRepository]
  val groupsRepository = injector.instance[GroupsRepository]

  test("findAll") {

    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
    val sessionUser = signUp("session name", "session password", udid).account
    val user1 = signUp("user name 1", "user password 1", udid).account
    val user2 = signUp("user name 2", "user password 2", udid).account
    val user3 = signUp("user name 3", "user password 3", udid).account
    val user4 = signUp("user name 4", "user password 4", udid).account
    val user5 = signUp("user name 5", "user password 5", udid).account

    Await.result(devicesRepository.update(udid, pushToken, sessionUser.id.toSessionId))
    // TODO : Check

    Await.result(devicesRepository.update(udid, pushToken, user1.id.toSessionId))
    Await.result(devicesRepository.update(udid, pushToken, user2.id.toSessionId))
    Await.result(devicesRepository.update(udid, pushToken, user3.id.toSessionId))
    Await.result(devicesRepository.update(udid, pushToken, user4.id.toSessionId))
    Await.result(devicesRepository.update(udid, pushToken, user5.id.toSessionId))

    val groupId = Await.result(groupsRepository.create(Some("group name"), true, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    val groupInviteIds = Await.result(groupInvitesRepository.create(List(user1.id, user2.id, user3.id, user4.id, user5.id), groupId, sessionUser.id.toSessionId))
    assert(groupInviteIds.size == 5)

    val result = Await.result(deliveryGroupInvitesRepository.findAll(groupInviteIds.head))
    assert(result.get.size == 1)

    assert(Await.result(deliveryGroupInvitesRepository.findAll(GroupInviteId(0L))).isEmpty == true)

    val result2 = Await.result(deliveryGroupInvitesRepository.updateNotified(groupInviteIds.head))
    assert(result2 == true)

  }


}
