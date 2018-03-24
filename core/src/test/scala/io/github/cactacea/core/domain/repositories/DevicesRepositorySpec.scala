package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO

class DevicesRepositorySpec extends RepositorySpec {

  val devicesRepository = injector.instance[DevicesRepository]
  val devicesDAO = injector.instance[DevicesDAO]

  test("updateDeviceToken") {

    val displayName = "new account"
    val password = "password"
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
    val account = signUp(displayName, password, udid)

    Await.result(devicesRepository.update(udid, pushToken, account.id.toSessionId))

    val devices = Await.result(devicesDAO.find(account.id.toSessionId))
    assert(devices.size == 1)

    val device = devices.head
    assert(device.pushToken == pushToken)

  }


}
