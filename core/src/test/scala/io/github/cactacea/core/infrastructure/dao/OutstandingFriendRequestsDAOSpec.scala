package io.github.cactacea.backend.core.infrastructure.dao


import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class OutstandingFriendRequestsDAOSpec extends DAOSpec {


  import db._

  test("create") {

    val sessionAccount = createAccount("OutstandingFriendRequestsDAOSpec1")
    val followAccount1 = createAccount("OutstandingFriendRequestsDAOSpec2")
    val followAccount2 = createAccount("OutstandingFriendRequestsDAOSpec3")

    // create friendRequestInProgresss
    execute(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    execute(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.inProgress == true)
    assert(result2.inProgress == true)

    // delete friendRequestInProgresss
    execute(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    execute(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))

    // create friendRequestInProgresss
    execute(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    execute(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))
    val result3 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result4 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result3.inProgress == true)
    assert(result4.inProgress == true)

  }

  test("delete") {

    val sessionAccount = createAccount("OutstandingFriendRequestsDAOSpec4")
    val followAccount1 = createAccount("OutstandingFriendRequestsDAOSpec5")
    val followAccount2 = createAccount("OutstandingFriendRequestsDAOSpec6")
    execute(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount1.id.toSessionId))
    execute(outstandingFriendRequestsDAO.create(sessionAccount.id, followAccount2.id.toSessionId))

    // delete friendRequestInProgresss
    execute(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount1.id.toSessionId))
    execute(outstandingFriendRequestsDAO.delete(sessionAccount.id, followAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount1.id))))).head
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(followAccount2.id))))).head
    assert(result1.inProgress == false)
    assert(result2.inProgress == false)

  }

}


