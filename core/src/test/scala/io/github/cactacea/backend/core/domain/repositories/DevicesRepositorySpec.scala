package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.RepositorySpec

class DevicesRepositorySpec extends RepositorySpec {

  val devicesRepository = injector.instance[DevicesRepository]

  test("updateDeviceToken") {

    val displayName = "DevicesRepositorySpec1"
    val password = "password"
    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken: Option[String] = Some("0000000000000000000000000000000000000000000000000000000000000000")
    val account = signUp(displayName, password, udid)

    execute(devicesRepository.update(udid, pushToken, account.id.toSessionId))

    val devices = execute(devicesDAO.find(account.id.toSessionId))
    assert(devices.size == 1)

    val device = devices.head
    assert(device.pushToken == pushToken)

  }


}
