package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, MediumType}
import io.github.cactacea.backend.core.helpers.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Messages

class MessagesDAOSpec extends DAOSpec {

  val messagesDAO: MessagesDAO = injector.instance[MessagesDAO]
  val groupsDAO: GroupsDAO = injector.instance[GroupsDAO]
  val mediumsDAO: MediumsDAO = injector.instance[MediumsDAO]

  import db._

  test("create") {

    val sessionAccount = createAccount("MediumsDAOSpec4")
    val groupId = Await.result(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = Await.result(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))

    val result = Await.result(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result.size == 1)
    val message = result(0)
    assert(message.id == messageId)
    assert(message.message == Some("new message"))
    assert(message.accountCount == 1L)
    assert(message.contentWarning == false)
    assert(message.notified == false)

    val mediumId2 = Await.result(mediumsDAO.create("key1", "http://example.com/test1.jpeg", Some("http://example.com/test1.jpeg"), MediumType.image, 1, 4, 100L, sessionAccount.id.toSessionId))
    Await.result(messagesDAO.create(groupId, None, 1, Some(mediumId2), sessionAccount.id.toSessionId))
    val result2 = Await.result(messagesDAO.find(messageId))
    assert(result2.isDefined)

  }

  test("delete") {

    val sessionAccount = createAccount("MediumsDAOSpec5")
    val groupId = Await.result(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = Await.result(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))

    Await.result(messagesDAO.delete(groupId))
    val result = Await.result(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result.size == 0)
  }

  test("updateReadStatus") {

    val sessionAccount = createAccount("MediumsDAOSpec6")
    val groupId = Await.result(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = Await.result(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    // TODO : check
    Await.result(messagesDAO.updateReadAccountCount(List(messageId)))

    val result2 = Await.result(db.run(quote(query[Messages].filter(_.groupId == lift(groupId)))))
    assert(result2.size == 1)
    val message = result2(0)
    assert(message.readAccountCount == 1)

  }

  test("updateNotified") {

    val sessionAccount = createAccount("MediumsDAOSpec7")
    val groupId = Await.result(groupsDAO.create(Some("new group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, 0L, sessionAccount.id.toSessionId))
    val messageId = Await.result(messagesDAO.create(groupId, Some("new message"), 1, None, sessionAccount.id.toSessionId))
    // TODO : Check
    Await.result(messagesDAO.updateNotified(messageId))

    val result2 = Await.result(db.run(quote(query[Messages].filter(_.id == lift(messageId)))))
    assert(result2.size == 1)
    val message = result2(0)
    assert(message.notified == true)

  }

}
