package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.ActiveStatusType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class DevicesDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a device") {
      forAll(accountGen, deviceGen, deviceGen) { (a, d1, d2) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        await(devicesDAO.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(devicesDAO.create(d2.udid, d2.pushToken, d2.deviceType, d2.userAgent, sessionId))
        val result = await(findDevice(sessionId))
        assert(result.size == 2)
        assert(result.headOption.map(_.udid) == Option(d2.udid))
        assert(result.headOption.map(_.userAgent) == Option(d2.userAgent))
        assert(result.headOption.map(_.deviceType) == Option(d2.deviceType))
        assert(result.lastOption.map(_.udid) == Option(d1.udid))
        assert(result.lastOption.map(_.userAgent) == Option(d1.userAgent))
        assert(result.lastOption.map(_.deviceType) == Option(d1.deviceType))
      }
    }
  }

  feature("delete") {
    scenario("should delete a device") {
      forAll(accountGen, deviceGen, deviceGen) { (a, d1, d2) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        await(devicesDAO.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(devicesDAO.create(d2.udid, d2.pushToken, d2.deviceType, d2.userAgent, sessionId))
        await(devicesDAO.delete(d1.udid, sessionId))
        val result = await(findDevice(sessionId))
        assert(result.size == 1)
        assert(result.headOption.map(_.udid) == Option(d2.udid))
        assert(result.headOption.map(_.userAgent) == Option(d2.userAgent))
        assert(result.headOption.map(_.deviceType) == Option(d2.deviceType))
      }
    }
  }

  feature("update") {
    scenario("should update device info") {
      forOne(accountGen, deviceGen, deviceGen) { (s, d1, d2) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        await(devicesDAO.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(devicesDAO.update(d1.udid, d2.pushToken, sessionId))
        val result = await(findDevice(sessionId)).headOption
        assert(result.exists(_.activeStatus == ActiveStatusType.active))
        assert(result.exists(_.pushToken == d2.pushToken))
      }
    }
  }

  feature("findActiveStatus") {
    scenario("should find active status") {
      forOne(accountGen, deviceGen, deviceGen) { (s, d1, d2) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        await(devicesDAO.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(devicesDAO.create(d2.udid, d2.pushToken, d2.deviceType, d2.userAgent, sessionId))
        val result1 = await(devicesDAO.findActiveStatus(sessionId.toAccountId))
        assert(result1.status == ActiveStatusType.active)
        await(devicesDAO.delete(d1.udid, sessionId))
        val result2 = await(devicesDAO.findActiveStatus(sessionId.toAccountId))
        assert(result2.status == ActiveStatusType.active)
        await(devicesDAO.delete(d2.udid, sessionId))
        val result3 = await(devicesDAO.findActiveStatus(sessionId.toAccountId))
        assert(result3.status == ActiveStatusType.inactive)
      }
    }
  }

}

