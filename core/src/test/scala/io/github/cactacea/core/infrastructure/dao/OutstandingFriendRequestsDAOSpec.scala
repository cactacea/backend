package io.github.cactacea.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec
import io.github.cactacea.core.infrastructure.models.Relationships

class OutstandingFriendRequestsDAOSpec extends DAOSpec {


  import db._

  val outstandingFriendRequestsDAO: FriendRequestsStatusDAO = injector.instance[FriendRequestsStatusDAO]

  test("create") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")

    // create friendRequestInProgresss
    Await.result(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.inProgress == true)
    assert(result2.inProgress == true)

    // delete friendRequestInProgresss
    Await.result(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))

    // create friendRequestInProgresss
    Await.result(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result4 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result3.inProgress == true)
    assert(result4.inProgress == true)

  }

  test("delete") {

    val sessionAccount = createAccount("account0")
    val followAccount1 = createAccount("account1")
    val followAccount2 = createAccount("account2")
    Await.result(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // delete friendRequestInProgresss
    Await.result(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    Await.result(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = Await.result(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.inProgress == false)
    assert(result2.inProgress == false)

  }

}


