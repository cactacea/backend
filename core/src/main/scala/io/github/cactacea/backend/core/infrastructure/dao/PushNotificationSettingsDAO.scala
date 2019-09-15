package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.models.PushNotificationSettings

@Singleton
class PushNotificationSettingsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(sessionId: SessionId): Future[Unit] = {

    val userId = sessionId.userId
    val q = quote {
      query[PushNotificationSettings].insert(
        _.userId           -> lift(userId),
        _.tweet                -> lift(true),
        _.comment             -> lift(true),
        _.friendRequest       -> lift(true),
        _.message             -> lift(true),
        _.channelMessage        -> lift(true),
        _.invitation     -> lift(true),
        _.showMessage         -> lift(true)
      )
    }
    run(q).map(_ => ())
  }

  def update(tweet: Boolean,
             comment:Boolean,
             friendRequest: Boolean,
             message: Boolean,
             channelMessage: Boolean,
             invitation: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {

    val userId = sessionId.userId
    val q = quote {
      query[PushNotificationSettings]
        .filter(_.userId == lift(userId))
        .update(
          _.tweet            -> lift(tweet),
          _.comment         -> lift(comment),
          _.friendRequest   -> lift(friendRequest),
          _.message         -> lift(message),
          _.channelMessage    -> lift(channelMessage),
          _.invitation      -> lift(invitation),
          _.showMessage     -> lift(showMessage)
        )
    }
    run(q).map(_ => ())
  }

  def find(sessionId: SessionId): Future[Option[PushNotificationSettings]] = {
    val userId = sessionId.userId
    val q = quote {
      query[PushNotificationSettings]
        .filter(_.userId == lift(userId))
    }
    run(q).map(_.headOption)
  }

}
