package io.github.cactacea.core.helpers

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.dao.{AccountsDAO, MediumsDAO}
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Mediums}

class CactaceaDAOTest extends CactaceaTest {

  val db = injector.instance[DatabaseService]

  def createAccount(userNo: Long): Accounts = {
    val u: Accounts = AccountsFactoryUtil.create(userNo)
    val id: SessionId = insertAccounts(u).toSessionId
    u.copy(id = id.toAccountId)
  }

  def createMedium(accountId: AccountId): Mediums = {
    val m: Mediums = MediumsFactoryUtil.create(accountId)
    val id = this.insertMediums(m)
    m.copy(id = id)
  }

  def selectAccounts(accountId: AccountId, sessionId: SessionId) = {
    val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]
    Await.result(
      accountsDAO.find(
        accountId,
        sessionId
      )
    )
  }

  def insertMediums(m: Mediums): MediumId = {
    val mediumsDAO: MediumsDAO = injector.instance[MediumsDAO]
    Await.result(
      mediumsDAO.create(
        m.key,
        m.uri,
        m.thumbnailUri,
        MediumType.forName(m.mediumType),
        m.width,
        m.height,
        m.size,
        m.by.toSessionId
      )
    )
  }

  def insertAccounts(a: Accounts): AccountId = {
    val accountsDAO: AccountsDAO = injector.instance[AccountsDAO]
    Await.result(
      accountsDAO.create(
        a.accountName,
        a.displayName,
        a.password,
        None,
        None,
        None,
        None
      )
    )
  }


}
