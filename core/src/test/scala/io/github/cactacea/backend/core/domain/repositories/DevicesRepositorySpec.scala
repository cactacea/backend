package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec

class DevicesRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a device") {
      forAll(userGen, deviceGen, deviceGen) { (a, d1, d2) =>
        val sessionId = await(usersRepository.create(a.userName)).id.sessionId
        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(devicesRepository.create(d2.udid, d2.pushToken, d2.deviceType, d2.userAgent, sessionId))
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

    scenario("should update a device") {
      forAll(userGen, deviceGen, deviceGen) { (a, d1, d2) =>
        val sessionId = await(usersRepository.create(a.userName)).id.sessionId
        await(devicesRepository.create(d1.udid, d1.pushToken, d1.deviceType, d1.userAgent, sessionId))
        await(devicesRepository.create(d1.udid, d2.pushToken, d2.deviceType, d2.userAgent, sessionId))
        val result = await(findDevice(sessionId))
        assert(result.size == 1)
        assert(result.headOption.map(_.udid) == Option(d1.udid))
        assert(result.headOption.map(_.userAgent) == Option(d1.userAgent))
        assert(result.headOption.map(_.deviceType) == Option(d1.deviceType))
        assert(result.headOption.map(_.pushToken) == Option(d2.pushToken))
      }

    }

  }

}
