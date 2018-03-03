package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.models.PushNotificationSettings
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class PushNotificationSettingsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(groupInvitation: Boolean, followerFeed: Boolean, feedComment:Boolean, groupMessage: Boolean, direcrtMessage: Boolean, showMessage: Boolean, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[PushNotificationSettings].insert(
        _.accountId           -> lift(accountId),
        _.groupInvitation         -> lift(groupInvitation),
        _.followerFeed        -> lift(followerFeed),
        _.feedComment         -> lift(feedComment),
        _.groupMessage        -> lift(groupMessage),
        _.directMessage       -> lift(direcrtMessage),
        _.showMessage         -> lift(showMessage)
      )
    }
    run(q).map(_ == 1)
  }

  def update(groupInvitation: Boolean, followerFeed: Boolean, feedComment:Boolean, groupMessage: Boolean, directMessage: Boolean, showMessage: Boolean, sessionId: SessionId): Future[Boolean] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[PushNotificationSettings]
        .filter(_.accountId == lift(accountId))
        .update(
          _.groupInvitation         -> lift(groupInvitation),
          _.followerFeed        -> lift(followerFeed),
          _.feedComment         -> lift(feedComment),
          _.groupMessage        -> lift(groupMessage),
          _.directMessage       -> lift(directMessage),
          _.showMessage         -> lift(showMessage)
        )
    }
    run(q).map(_ == 1)
  }

  def find(sessionId: SessionId): Future[Option[PushNotificationSettings]] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[PushNotificationSettings]
        .filter(_.accountId == lift(accountId))
    }
    run(q).map(_.headOption)
  }

}
