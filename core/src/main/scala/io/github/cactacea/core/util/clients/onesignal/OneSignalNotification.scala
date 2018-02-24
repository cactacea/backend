package io.github.cactacea.core.infrastructure.clients.onesignal

import io.github.cactacea.core.infrastructure.identifiers._

case class OneSignalNotification (
                                   appId: String,
                                   contents: OneSignalNotificationContent,
                                   includePlayerIds: List[String],
                                   accountId: AccountId,
                                   displayName: String,
                                   groupId: Option[GroupId],
                                   invitedAt: Option[Long],
                                   feedId: Option[FeedId],
                                   commentId: Option[CommentId],
                                   messageId: Option[MessageId],
                                   postedAt: Option[Long]
                                 )