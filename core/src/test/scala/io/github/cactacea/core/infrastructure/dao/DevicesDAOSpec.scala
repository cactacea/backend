package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.CactaceaDAOTest
import io.github.cactacea.core.infrastructure.models.Devices

class DevicesDAOSpec extends CactaceaDAOTest {

  import db._

  val devicesDAO: DevicesDAO = injector.instance[DevicesDAO]

  test("create") {

    val sessionAccount = this.createAccount(0L)

    val udid = "udid"
    val deviceId = Await.result(devicesDAO.create(udid, None, sessionAccount.id.toSessionId))
    val devices = Await.result(db.run(quote(query[Devices].filter(_.id == lift(deviceId)))))
    val device = devices.head
    assert(devices.size == 1)
    assert(device.id == deviceId)
    assert(device.userAgent == None)

  }

  test("find") {

    val sessionAccount = this.createAccount(0L)

    val udid = "udid"
    val deviceId = Await.result(devicesDAO.create(udid, None, sessionAccount.id.toSessionId))

    val devices = Await.result(devicesDAO.find(sessionAccount.id.toSessionId))
    val device = devices.head
    assert(devices.size == 1)
    assert(device.id == deviceId)
    assert(device.userAgent == None)

  }

  test("edit") {

    val sessionAccount = this.createAccount(0L)

    val udid = "740f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad"
    val pushToken = Some("0000000000000000000000000000000000000000000000000000000000000000")
    val deviceId = Await.result(devicesDAO.create(udid, None, sessionAccount.id.toSessionId))

    Await.result(devicesDAO.update(udid, pushToken, sessionAccount.id.toSessionId))
    val devices = Await.result(db.run(quote(query[Devices].filter(_.id == lift(deviceId)))))
    val device = devices.head
    assert(devices.size == 1)
    assert(device.pushToken == pushToken)

  }

  test("exist") {

    val sessionAccount = this.createAccount(0L)

    val udid = "udid"
    Await.result(devicesDAO.create(udid, None, sessionAccount.id.toSessionId))
    val result = Await.result(devicesDAO.exist(sessionAccount.id.toSessionId, udid))
    assert(result == true)

  }



}