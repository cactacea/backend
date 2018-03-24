package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FriendRequestsStatusDAOSpec extends DAOSpec {

  import db._

  val friendRequestsStatusDAO: FriendRequestsStatusDAO = injector.instance[FriendRequestsStatusDAO]

  test("create") {

    val sessionAccount = createAccount("account0")
    val requestingAccount1 = createAccount("account1")
    val requestingAccount2 = createAccount("account2")
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))

    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount1.id))))).headOption
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount2.id))))).headOption
    assert(result1.isDefined == true)
    assert(result2.isDefined == true)

    Await.result(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount2.id.toSessionId))

    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    Await.result(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))

    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount1.id))))).headOption
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount2.id))))).headOption
    assert(result3.isDefined == true)
    assert(result4.isDefined == true)

  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val requestingAccount1 = createAccount("account1")
    val requestingAccount2 = createAccount("account2")
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