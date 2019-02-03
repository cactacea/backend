package io.github.cactacea.backend.core.helpers

import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.logging.Logging
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.core.application.components.interfaces.HashService
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.dao.{PushNotificationCommentsDAO, PushNotificationFriendRequestsDAO, PushNotificationGroupInvitationsDAO, _}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Mediums}
import io.github.cactacea.core.helpers.HelperDAO
import org.scalatest.BeforeAndAfter

class DAOSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultHashModule,
        DefaultDeepLinkModule
      )
    ).create

  def execute[T](f: => Future[T]): T = {
    Await.result(db.transaction(f))
  }

  val db: DatabaseService = injector.instance[DatabaseService]

  val accountsDAO = injector.instance[AccountsDAO]
  val mediumsDAO = injector.instance[MediumsDAO]
  val feedsDAO = injector.instance[FeedsDAO]
  val accountFeedsDAO = injector.instance[AccountFeedsDAO]
  val groupsDAO = injector.instance[GroupsDAO]
  val accountGroupsDAO = injector.instance[AccountGroupsDAO]
  val messagesDAO = injector.instance[MessagesDAO]
  val accountMessagesDAO = injector.instance[AccountMessagesDAO]
  val userReportsDAO = injector.instance[AccountReportsDAO]
  val followingsDAO = injector.instance[FollowingsDAO]
  val friendsDAO = injector.instance[FriendsDAO]
  val hashService = injector.instance[HashService]
  val commentLikesDAO = injector.instance[CommentLikesDAO]
  val commentReportsDAO = injector.instance[CommentReportsDAO]
  val pushNotificationSettingDAO = injector.instance[PushNotificationSettingsDAO]
  val devicesDAO = injector.instance[DevicesDAO]
  val feedLikesDAO = injector.instance[FeedLikesDAO]
  val feedReportsDAO = injector.instance[FeedReportsDAO]
  val commentsDAO = injector.instance[CommentsDAO]
  val followersDAO = injector.instance[FollowersDAO]
  val friendRequestsDAO = injector.instance[FriendRequestsDAO]
  val friendRequestsStatusDAO = injector.instance[FriendRequestsStatusDAO]
  val groupAccountsDAO = injector.instance[GroupAccountsDAO]
  val groupAuthorityDAO = injector.instance[GroupAuthorityDAO]
  val groupInvitationsDAO = injector.instance[GroupInvitationsDAO]
  val groupReportsDAO = injector.instance[GroupReportsDAO]
  val blocksDAO = injector.instance[BlocksDAO]
  val mutesDAO = injector.instance[MutesDAO]
  val outstandingFriendRequestsDAO = injector.instance[FriendRequestsStatusDAO]
  val pushNotificationSettingsDAO = injector.instance[PushNotificationSettingsDAO]
  val pushNotificationFeedsDAO = injector.instance[PushNotificationFeedsDAO]
  val pushNotificationCommentsDAO = injector.instance[PushNotificationCommentsDAO]
  val pushNotificationMessagesDAO = injector.instance[PushNotificationMessagesDAO]
  val pushNotificationGroupInvitationsDAO = injector.instance[PushNotificationGroupInvitationsDAO]
  val pushNotificationFriendRequestsDAO = injector.instance[PushNotificationFriendRequestsDAO]

  val helperDAO = injector.instance[HelperDAO]

  def createAccount(accountName: String): Accounts = {
    val u: Accounts = FactoryHelper.createAccounts(accountName)
    val id: SessionId = insertAccounts(u).toSessionId
    u.copy(id = id.toAccountId)
  }

  def createMedium(accountId: AccountId): Mediums = {
    val m: Mediums = FactoryHelper.createMediums(accountId)
    val id = insertMediums(m)
    m.copy(id = id)
  }

  def selectAccounts(accountId: AccountId, sessionId: SessionId) = {
    execute(
      accountsDAO.find(
        accountId,
        sessionId
      )
    )
  }

  def insertMediums(m: Mediums): MediumId = {
    execute(
      db.transaction(
        mediumsDAO.create(
          m.key,
          m.uri,
          m.thumbnailUrl,
          m.mediumType,
          m.width,
          m.height,
          m.size,
          m.by.toSessionId
        )
      )
    )
  }

  def insertAccounts(a: Accounts): AccountId = {
    execute(
      db.transaction(
        accountsDAO.create(
          a.accountName,
          a.password
        )
      )
    )
  }

}
