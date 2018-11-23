package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.core.infrastructure.models.Mediums

class MediumsDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("MediumsDAOSpec1")
    val mediumId1 = execute(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    val mediumId2 = execute(mediumsDAO.create("key2", "http://example.com/test2.mov", Some("http://example.com/test2.mov"), MediumType.movie, 2, 5, 200L, sessionAccount.id.toSessionId))
    val mediumId3 = execute(mediumsDAO.create("key3", "http://example.com/test3.jpeg", Some("http://example.com/test3.jpeg"), MediumType.image, 3, 6, 300L, sessionAccount.id.toSessionId))

    val result = execute(db.run(quote(query[Mediums].filter(_.by == lift(sessionAccount.id)).sortBy(_.id)(Ord.asc))))
    assert(result.size == 3)
    val medium1 = result(0)
    val medium2 = result(1)
    val medium3 = result(2)

    assert(medium1.id == mediumId1)
    assert(medium2.id == mediumId2)
    assert(medium3.id == mediumId3)

    assert(medium1.by == sessionAccount.id)
    assert(medium2.by == sessionAccount.id)
    assert(medium3.by == sessionAccount.id)

    assert(medium1.width == 1)
    assert(medium2.width == 2)
    assert(medium3.width == 3)

    assert(medium1.height == 4)
    assert(medium2.height == 5)
    assert(medium3.height == 6)

    assert(medium1.size == 100L)
    assert(medium2.size == 200L)
    assert(medium3.size == 300L)

    assert(medium1.key == "key1")
    assert(medium2.key == "key2")
    assert(medium3.key == "key3")

    assert(medium1.mediumType == MediumType.image)
    assert(medium2.mediumType == MediumType.movie)
    assert(medium3.mediumType == MediumType.image)

    assert(medium1.uri == "http://example.com/test1.jpeg")
    assert(medium2.uri == "http://example.com/test2.mov")
    assert(medium3.uri == "http://example.com/test3.jpeg")

    assert(medium1.thumbnailUri == Some("http://example.com/test1.jpeg"))
    assert(medium2.thumbnailUri == Some("http://example.com/test2.mov"))
    assert(medium3.thumbnailUri == Some("http://example.com/test3.jpeg"))


  }

  test("exist") {

    val sessionAccount = createAccount("MediumsDAOSpec2")
    val mediumId1 = execute(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    val mediumId2 = execute(mediumsDAO.create("key2", "http://example.com/test2.mov", Some("http://example.com/test2.mov"), MediumType.movie, 2, 5, 200L, sessionAccount.id.toSessionId))
    val mediumId3 = execute(mediumsDAO.create("key3", "http://example.com/test3.jpeg", Some("http://example.com/test3.jpeg"), MediumType.image, 3, 6, 300L, sessionAccount.id.toSessionId))
    val mediumId4 = MediumId(-1L)

    val result1 = execute(mediumsDAO.exist(mediumId1, sessionAccount.id.toSessionId))
    val result2 = execute(mediumsDAO.exist(mediumId2, sessionAccount.id.toSessionId))
    val result3 = execute(mediumsDAO.exist(mediumId3, sessionAccount.id.toSessionId))
    val result4 = execute(mediumsDAO.exist(mediumId4, sessionAccount.id.toSessionId))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == false)

  }

  test("exist ids") {

    val sessionAccount = createAccount("MediumsDAOSpec3")
    val mediumId1 = execute(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    val mediumId2 = execute(mediumsDAO.create("key2", "http://example.com/test2.mov", Some("http://example.com/test2.mov"), MediumType.movie, 2, 5, 200L, sessionAccount.id.toSessionId))
    val mediumId3 = execute(mediumsDAO.create("key3", "http://example.com/test3.jpeg", Some("http://example.com/test3.jpeg"), MediumType.image, 3, 6, 300L, sessionAccount.id.toSessionId))
    val mediumId4 = MediumId(-1L)

    val result1 = execute(mediumsDAO.exist(List(mediumId1, mediumId2, mediumId3), sessionAccount.id.toSessionId))
    val result2 = execute(mediumsDAO.exist(List(mediumId1, mediumId2, mediumId3, mediumId4), sessionAccount.id.toSessionId))

    assert(result1 == true)
    assert(result2 == false)

  }

}
