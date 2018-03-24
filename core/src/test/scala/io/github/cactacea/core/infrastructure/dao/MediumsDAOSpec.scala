package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.MediumType
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.core.infrastructure.models.Mediums

class MediumsDAOSpec extends DAOSpec {

  val mediumsDAO: MediumsDAO = injector.instance[MediumsDAO]

  import db._

  test("create") {

    val sessionAccount = createAccount("account0")
    val mediumId1 = Await.result(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    val mediumId2 = Await.result(mediumsDAO.create("key2", "http://example.com/test2.mov", Some("http://example.com/test2.mov"), MediumType.movie, 2, 5, 200L, sessionAccount.id.toSessionId))
    val mediumId3 = Await.result(mediumsDAO.create("key3", "http://example.com/test3.jpeg", Some("http://example.com/test3.jpeg"), MediumType.image, 3, 6, 300L, sessionAccount.id.toSessionId))

    val result = Await.result(db.run(quote(query[Mediums].sortBy(_.id)(Ord.ascNullsLast))))
    assert(result.size == 3)
    val medium1 = result(0)
    val medium2 = result(1)
    val medium3 = result(2)

    assert((medium1.id, medium1.by, medium1.width, medium1.height, medium1.size, medium1.key, medium1.mediumType, medium1.uri, medium1.thumbnailUri) == (mediumId1, sessionAccount.id, 1, 4, 100L, "key1", MediumType.image, "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg")))
    assert((medium2.id, medium2.by, medium2.width, medium2.height, medium2.size, medium2.key, medium2.mediumType, medium2.uri, medium2.thumbnailUri) == (mediumId2, sessionAccount.id, 2, 5, 200L, "key2", MediumType.movie, "http://example.com/test2.mov", Some("http://example.com/test2.mov")))
    assert((medium3.id, medium3.by, medium3.width, medium3.height, medium3.size, medium3.key, medium3.mediumType, medium3.uri, medium3.thumbnailUri) == (mediumId3, sessionAccount.id, 3, 6, 300L, "key3", MediumType.image, "http://example.com/test3.jpeg", Some("http://example.com/test3.jpeg")))

  }

  test("exist") {

    val sessionAccount = createAccount("account0")
    val mediumId1 = Await.result(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    val mediumId2 = Await.result(mediumsDAO.create("key2", "http://example.com/test2.mov", Some("http://example.com/test2.mov"), MediumType.movie, 2, 5, 200L, sessionAccount.id.toSessionId))
    val mediumId3 = Await.result(mediumsDAO.create("key3", "http://example.com/test3.jpeg", Some("http://example.com/test3.jpeg"), MediumType.image, 3, 6, 300L, sessionAccount.id.toSessionId))
    val mediumId4 = MediumId(-1L)

    val result1 = Await.result(mediumsDAO.exist(mediumId1, sessionAccount.id.toSessionId))
    val result2 = Await.result(mediumsDAO.exist(mediumId2, sessionAccount.id.toSessionId))
    val result3 = Await.result(mediumsDAO.exist(mediumId3, sessionAccount.id.toSessionId))
    val result4 = Await.result(mediumsDAO.exist(mediumId4, sessionAccount.id.toSessionId))

    assert(result1 == true)
    assert(result2 == true)
    assert(result3 == true)
    assert(result4 == false)

  }

  test("exist ids") {

    val sessionAccount = createAccount("account0")
    val mediumId1 = Await.result(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    val mediumId2 = Await.result(mediumsDAO.create("key2", "http://example.com/test2.mov", Some("http://example.com/test2.mov"), MediumType.movie, 2, 5, 200L, sessionAccount.id.toSessionId))
    val mediumId3 = Await.result(mediumsDAO.create("key3", "http://example.com/test3.jpeg", Some("http://example.com/test3.jpeg"), MediumType.image, 3, 6, 300L, sessionAccount.id.toSessionId))
    val mediumId4 = MediumId(-1L)

    val result1 = Await.result(mediumsDAO.exist(List(mediumId1, mediumId2, mediumId3), sessionAccount.id.toSessionId))
    val result2 = Await.result(mediumsDAO.exist(List(mediumId1, mediumId2, mediumId3, mediumId4), sessionAccount.id.toSessionId))

    assert(result1 == true)
    assert(result2 == false)

  }

}
