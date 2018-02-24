package io.github.cactacea.core.domain.repositories

import io.github.cactacea.core.helpers.SessionRepositoryTest

class TimelineRepositorySpec extends SessionRepositoryTest {

  val timelineRepository = injector.instance[TimelineRepository]

  test("find session's timeline") (pending)

}
