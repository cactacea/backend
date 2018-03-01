package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.models.AdvertisementSettings

class AdvertisementSettingsDAOSpec extends DAOSpec {

  val advertisementSettingsDAO: AdvertisementSettingsDAO = injector.instance[AdvertisementSettingsDAO]

  import db._

  test("create") {

    val sessionAccount1 = this.createAccount(1L)
    val sessionAccount2 = this.createAccount(2L)
    val sessionAccount3 = this.createAccount(3L)
    val sessionAccount4 = this.createAccount(4L)
    val sessionAccount5 = this.createAccount(5L)

    // create settings
    Await.result(advertisementSettingsDAO.create(true, false, false, false, false, sessionAccount1.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, true, false, false, false, sessionAccount2.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, true, false, false, sessionAccount3.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, false, true, false, sessionAccount4.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, false, false, true, sessionAccount5.id.toSessionId))
    val ad1 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val ad2 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val ad3 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val ad4 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val ad5 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    assert((ad1.ad1, ad1.ad2, ad1.ad3, ad1.ad4, ad1.ad5) == (true, false, false, false, false))
    assert((ad2.ad1, ad2.ad2, ad2.ad3, ad2.ad4, ad2.ad5) == (false, true, false, false, false))
    assert((ad3.ad1, ad3.ad2, ad3.ad3, ad3.ad4, ad3.ad5) == (false, false, true, false, false))
    assert((ad4.ad1, ad4.ad2, ad4.ad3, ad4.ad4, ad4.ad5) == (false, false, false, true, false))
    assert((ad5.ad1, ad5.ad2, ad5.ad3, ad5.ad4, ad5.ad5) == (false, false, false, false, true))

  }

  test("find") {

    val sessionAccount1 = this.createAccount(1L)
    val sessionAccount2 = this.createAccount(2L)
    val sessionAccount3 = this.createAccount(3L)
    val sessionAccount4 = this.createAccount(4L)
    val sessionAccount5 = this.createAccount(5L)

    // create settings
    Await.result(advertisementSettingsDAO.create(true, false, false, false, false, sessionAccount1.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, true, false, false, false, sessionAccount2.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, true, false, false, sessionAccount3.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, false, true, false, sessionAccount4.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, false, false, true, sessionAccount5.id.toSessionId))
    val ad1 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val ad2 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val ad3 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val ad4 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val ad5 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    assert((ad1.ad1, ad1.ad2, ad1.ad3, ad1.ad4, ad1.ad5) == (true, false, false, false, false))
    assert((ad2.ad1, ad2.ad2, ad2.ad3, ad2.ad4, ad2.ad5) == (false, true, false, false, false))
    assert((ad3.ad1, ad3.ad2, ad3.ad3, ad3.ad4, ad3.ad5) == (false, false, true, false, false))
    assert((ad4.ad1, ad4.ad2, ad4.ad3, ad4.ad4, ad4.ad5) == (false, false, false, true, false))
    assert((ad5.ad1, ad5.ad2, ad5.ad3, ad5.ad4, ad5.ad5) == (false, false, false, false, true))

  }

  test("edit") {

    val sessionAccount1 = this.createAccount(1L)
    val sessionAccount2 = this.createAccount(2L)
    val sessionAccount3 = this.createAccount(3L)
    val sessionAccount4 = this.createAccount(4L)
    val sessionAccount5 = this.createAccount(5L)

    // create settings
    Await.result(advertisementSettingsDAO.create(true, false, false, false, false, sessionAccount1.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, true, false, false, false, sessionAccount2.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, true, false, false, sessionAccount3.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, false, true, false, sessionAccount4.id.toSessionId))
    Await.result(advertisementSettingsDAO.create(false, false, false, false, true, sessionAccount5.id.toSessionId))
    val ad1 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val ad2 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val ad3 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val ad4 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val ad5 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    assert((ad1.ad1, ad1.ad2, ad1.ad3, ad1.ad4, ad1.ad5) == (true, false, false, false, false))
    assert((ad2.ad1, ad2.ad2, ad2.ad3, ad2.ad4, ad2.ad5) == (false, true, false, false, false))
    assert((ad3.ad1, ad3.ad2, ad3.ad3, ad3.ad4, ad3.ad5) == (false, false, true, false, false))
    assert((ad4.ad1, ad4.ad2, ad4.ad3, ad4.ad4, ad4.ad5) == (false, false, false, true, false))
    assert((ad5.ad1, ad5.ad2, ad5.ad3, ad5.ad4, ad5.ad5) == (false, false, false, false, true))

    // edit settings
    Await.result(advertisementSettingsDAO.update(false, false, false, false, true, sessionAccount1.id.toSessionId))
    Await.result(advertisementSettingsDAO.update(false, false, false, true, false, sessionAccount2.id.toSessionId))
    Await.result(advertisementSettingsDAO.update(false, true, false, false, false, sessionAccount3.id.toSessionId))
    Await.result(advertisementSettingsDAO.update(false, false, true, false, false, sessionAccount4.id.toSessionId))
    Await.result(advertisementSettingsDAO.update(true, false, false, false, false, sessionAccount5.id.toSessionId))
    val ed1 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount1.id))))).head
    val ed2 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount2.id))))).head
    val ed3 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount3.id))))).head
    val ed4 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount4.id))))).head
    val ed5 = Await.result(db.run(quote(query[AdvertisementSettings].filter(_.accountId == lift(sessionAccount5.id))))).head
    assert((ed1.ad1, ed1.ad2, ed1.ad3, ed1.ad4, ed1.ad5) == (false, false, false, false, true))
    assert((ed2.ad1, ed2.ad2, ed2.ad3, ed2.ad4, ed2.ad5) == (false, false, false, true, false))
    assert((ed3.ad1, ed3.ad2, ed3.ad3, ed3.ad4, ed3.ad5) == (false, true, false, false, false))
    assert((ed4.ad1, ed4.ad2, ed4.ad3, ed4.ad4, ed4.ad5) == (false, false, true, false, false))
    assert((ed5.ad1, ed5.ad2, ed5.ad3, ed5.ad4, ed5.ad5) == (true, false, false, false, false))

    // find settings
    val fd1 = Await.result(advertisementSettingsDAO.find(sessionAccount1.id.toSessionId)).head
    val fd2 = Await.result(advertisementSettingsDAO.find(sessionAccount2.id.toSessionId)).head
    val fd3 = Await.result(advertisementSettingsDAO.find(sessionAccount3.id.toSessionId)).head
    val fd4 = Await.result(advertisementSettingsDAO.find(sessionAccount4.id.toSessionId)).head
    val fd5 = Await.result(advertisementSettingsDAO.find(sessionAccount5.id.toSessionId)).head
    val fd6 = Await.result(advertisementSettingsDAO.find(SessionId(0L)))
    assert((fd1.ad1, fd1.ad2, fd1.ad3, fd1.ad4, fd1.ad5) == (false, false, false, false, true))
    assert((fd2.ad1, fd2.ad2, fd2.ad3, fd2.ad4, fd2.ad5) == (false, false, false, true, false))
    assert((fd3.ad1, fd3.ad2, fd3.ad3, fd3.ad4, fd3.ad5) == (false, true, false, false, false))
    assert((fd4.ad1, fd4.ad2, fd4.ad3, fd4.ad4, fd4.ad5) == (false, false, true, false, false))
    assert((fd5.ad1, fd5.ad2, fd5.ad3, fd5.ad4, fd5.ad5) == (true, false, false, false, false))
    assert(fd6.isEmpty)
  }


}
