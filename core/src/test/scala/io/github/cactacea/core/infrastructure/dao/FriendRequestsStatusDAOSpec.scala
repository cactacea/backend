package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FriendRequestsStatusDAOSpec extends DAOSpec {

  import db._

  test("create") {

    val sessionAccount = createAccount("FriendRequestsStatusDAOSpec1")
    val requestingAccount1 = createAccount("FriendRequestsStatusDAOSpec2")
    val requestingAccount2 = createAccount("FriendRequestsStatusDAOSpec3")
    execute(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    execute(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))

    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount1.id))))).headOption
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount2.id))))).headOption
    assert(result1.isDefined == true)
    assert(result2.isDefined == true)

    execute(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount1.id.toSessionId))
    execute(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount2.id.toSessionId))

    execute(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    execute(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))

    val result3 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount1.id))))).headOption
    val result4 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.inProgress == true).filter(_.by == lift(requestingAccount2.id))))).headOption
    assert(result3.isDefined == true)
    assert(result4.isDefined == true)

  }

  test("delete") {

    val sessionAccount = createAccount("FriendRequestsStatusDAOSpec4")
    val requestingAccount1 = createAccount("FriendRequestsStatusDAOSpec5")
    val requestingAccount2 = createAccount("FriendRequestsStatusDAOSpec6")
    execute(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount1.id.toSessionId))
    execute(friendRequestsStatusDAO.create(sessionAccount.id, requestingAccount2.id.toSessionId))
    execute(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount1.id.toSessionId))
    execute(friendRequestsStatusDAO.delete(sessionAccount.id, requestingAccount2.id.toSessionId))
    val result1 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(requestingAccount1.id))))).head
    val result2 = execute(db.run(quote(query[Relationships].filter(_.accountId == lift(sessionAccount.id)).filter(_.by == lift(requestingAccount2.id))))).head
    assert(result1.inProgress == false)
    assert(result2.inProgress == false)

  }

}