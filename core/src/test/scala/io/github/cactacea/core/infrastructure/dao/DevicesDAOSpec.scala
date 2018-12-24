package io.github.cactacea.backend.core.infrastructure.dao


import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, DeviceType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Devices

class DevicesDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("DevicesDAOSpec1")

    val udid = "udid"
    val deviceId = execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount.id.toSessionId))
    val devices = execute(db.run(quote(query[Devices].filter(_.id == lift(deviceId)))))
    val device = devices.head
    assert(devices.size == 1)
    assert(device.id == deviceId)
    assert(device.userAgent == None)
    assert(device.activeStatus == ActiveStatusType.inactive)

  }

  test("find") {

    val sessionAccount = createAccount("DevicesDAOSpec2")

    val udid = "udid"
    val deviceId = execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount.id.toSessionId))

    val devices = execute(devicesDAO.find(sessionAccount.id.toSessionId))
    val device = devices.head
    assert(devices.size == 1)
    assert(device.id == deviceId)
    assert(device.userAgent == None)

  }

  test("edit") {

    val sessionAccount = createAccount("DevicesDAOSpec3")

    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken = Some("0000000000000000000000000000000000000000000000000000000000000000")
    val deviceId = execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount.id.toSessionId))

    execute(devicesDAO.update(udid, pushToken, sessionAccount.id.toSessionId))
    val devices = execute(db.run(quote(query[Devices].filter(_.id == lift(deviceId)))))
    val device = devices.head
    assert(devices.size == 1)
    assert(device.pushToken == pushToken)

  }

  test("exist") {

    val sessionAccount = createAccount("DevicesDAOSpec4")

    val udid = "udid"
    execute(devicesDAO.create(udid, DeviceType.ios, None, sessionAccount.id.toSessionId))
    val result = execute(devicesDAO.exist(sessionAccount.id.toSessionId, udid))
    assert(result == true)

  }



}