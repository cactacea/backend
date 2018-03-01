package io.github.cactacea.core.domain.repositories

import io.github.cactacea.core.helpers.RepositorySpec

class TimelineRepositorySpec extends RepositorySpec {

  val timelineRepository = injector.instance[TimelineRepository]

  test("find session's timeline") (pending)

}
