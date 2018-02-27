package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.Relationships

class FriendRequestsStatusDAOSpec extends DAOSpec {

  import db._

  val friendRequestsStatusDAO: FriendRequestsStatusDAO = injector.instance[FriendRequestsStatusDAO]

  test("create") {

    val sessionAccount = this.createAccount(0L)
    val requestingAccount1 = this.createAccount(1L)
    val requestingAccount2 = this.createAccount(2L)
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))

    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == lift(true)).filter(_.by == lift(requestingAccount1.id))))).headOption
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == lift(true)).filter(_.by == lift(requestingAccount2.id))))).headOption
    assert(result1.isDefined == true)
    assert(result2.isDefined == true)

    Await.result(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount2.id.toSessionId))

    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))

    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == lift(true)).filter(_.by == lift(requestingAccount1.id))))).headOption
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == lift(true)).filter(_.by == lift(requestingAccount2.id))))).headOption
    assert(result3.isDefined == true)
    assert(result4.isDefined == true)

  }

  test("delete") {

    val sessionAccount = this.createAccount(0L)
    val requestingAccount1 = this.createAccount(1L)
    val requestingAccount2 = this.createAccount(2L)
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))
    Await.result(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(requestingAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(requestingAccount2.id))))).head
    assert(result1.inProgress == false)
    assert(result2.inProgress == false)

  }

}