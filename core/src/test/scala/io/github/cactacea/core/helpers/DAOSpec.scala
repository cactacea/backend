package io.github.cactacea.backend.core.helpers

import com.google.inject.Inject
import com.twitter.inject.IntegrationTest
import com.twitter.inject.app.TestInjector
import com.twitter.util.logging.Logging
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.core.application.components.interfaces.HashService
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Mediums}
import org.scalatest.BeforeAndAfter

class DAOSpec extends IntegrationTest with BeforeAndAfter with Logging {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseProviderModule,
        DefaultHashModule
      )
    ).create

  def execute[T](f: => Future[T]) = {
    Await.result(db.transaction(f))
  }

  val db: DatabaseService = injector.instance[DatabaseService]

  @Inject var accountsDAO: AccountsDAO = _
  @Inject var mediumsDAO: MediumsDAO = _
  @Inject var feedsDAO: FeedsDAO = _
  @Inject var accountFeedsDAO: AccountFeedsDAO = _
  @Inject var groupsDAO: GroupsDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _
  @Inject var messagesDAO: MessagesDAO = _
  @Inject var accountMessagesDAO: AccountMessagesDAO = _
  @Inject var userReportsDAO: AccountReportsDAO = _
  @Inject var followsDAO: FollowsDAO = _
  @Inject var friendsDAO: FriendsDAO = _
  @Inject var hashService: HashService = _
  @Inject var commentLikesDAO: CommentLikesDAO = _
  @Inject var commentReportsDAO: CommentReportsDAO = _
  @Inject var pushNotificationSettingDAO: PushNotificationSettingsDAO = _
  @Inject var devicesDAO: DevicesDAO = _
  @Inject var pushNotificationsDAO: PushNotificationsDAO = _
  @Inject var feedLikesDAO: FeedLikesDAO = _
  @Inject var feedReportsDAO: FeedReportsDAO = _
  @Inject var commentsDAO: CommentsDAO = _
  @Inject var followersDAO: FollowersDAO = _
  @Inject var friendRequestsDAO: FriendRequestsDAO = _
  @Inject var friendRequestsStatusDAO: FriendRequestsStatusDAO = _
  @Inject var groupUsersDAO: GroupAccountsDAO = _
  @Inject var groupAuthorityDAO: GroupAuthorityDAO = _
  @Inject var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject var groupReportsDAO: GroupReportsDAO = _
  @Inject var blocksDAO: BlocksDAO = _
  @Inject var mutesDAO: MutesDAO = _
  @Inject var outstandingFriendRequestsDAO: FriendRequestsStatusDAO = _
  @Inject var groupAccountsDAO: GroupAccountsDAO = _
  @Inject var pushNotificationSettingsDAO: PushNotificationSettingsDAO = _
  @Inject var socialAccountsDAO: SocialAccountsDAO = injector.instance[SocialAccountsDAO]

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
          m.thumbnailUri,
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
          a.displayName,
          a.password,
          None,
          None,
          None,
          None
        )
      )
    )
  }


}
