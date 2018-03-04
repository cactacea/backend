package io.github.cactacea.core.helpers

import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import com.twitter.util.logging.Logging
import io.github.cactacea.core.application.components.modules._
import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.dao.{AccountsDAO, MediumsDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.core.infrastructure.models.{Accounts, Mediums}
import io.github.cactacea.core.infrastructure.services.{DatabaseProviderModule, DatabaseService}
import org.scalatest.BeforeAndAfter

class DAOSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultInjectionModule,
        DefaultConfigModule,
        DefaultFanOutModule,
        DefaultPushNotificationMessagesModule,
        DefaultNotificationModule,
        DefaultPublishModule,
        DefaultPushNotificationModule,
        DefaultStorageModule,
        DefaultSubScribeModule,
        DefaultTranscodeModule,
        FinatraJacksonModule
      )
    ).create

  before {
    DatabaseHelper.initialize()
  }

  after {
    DatabaseHelper.initialize()
  }

  val db = injector.instance[DatabaseService]

  def createAccount(userNo: Long): Accounts = {
    val u: Accounts = FactoryHelper.createAccounts(userNo)
    val id: SessionId = insertAccounts(u).toSessionId
    u.copy(id = id.toAccountId)
  }

  def createMedium(accountId: AccountId): Mediums = {
    val m: Mediums = FactoryHelper.createMediums(accountId)
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