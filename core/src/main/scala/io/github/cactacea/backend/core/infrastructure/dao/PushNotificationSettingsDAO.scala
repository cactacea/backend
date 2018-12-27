package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.PushNotificationSettings

@Singleton
class PushNotificationSettingsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(feed: Boolean,
             comment:Boolean,
             friendRequest: Boolean,
             message: Boolean,
             groupMessage: Boolean,
             groupInvitation: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {

    val accountId = sessionId.toAccountId
    val q = quote {
      query[PushNotificationSettings].insert(
        _.accountId           -> lift(accountId),
        _.feed                -> lift(feed),
        _.comment             -> lift(comment),
        _.friendRequest       -> lift(friendRequest),
        _.message             -> lift(message),
        _.groupMessage        -> lift(groupMessage),
        _.groupInvitation     -> lift(groupInvitation),
        _.showMessage         -> lift(showMessage)
      )
    }
    run(q).map(_ => Unit)
  }

  def update(feed: Boolean,
             comment:Boolean,
             friendRequest: Boolean,
             message: Boolean,
             groupMessage: Boolean,
             groupInvitation: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {

    val accountId = sessionId.toAccountId
    val q = quote {
      query[PushNotificationSettings]
        .filter(_.accountId == lift(accountId))
        .update(
          _.feed            -> lift(feed),
          _.comment         -> lift(comment),
          _.friendRequest   -> lift(friendRequest),
          _.message         -> lift(message),
          _.groupMessage    -> lift(groupMessage),
          _.groupInvitation -> lift(groupInvitation),
          _.showMessage     -> lift(showMessage)
        )
    }
    run(q).map(_ => Unit)
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
