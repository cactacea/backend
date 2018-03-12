package io.github.cactacea.backend.models.requests.identifiers

import io.github.cactacea.core.infrastructure.{identifiers => core}
import io.github.cactacea.backend.models.requests.{identifiers => backend}

// https://github.com/FasterXML/jackson-module-scala/issues/209

object IdentityConverter {

  implicit def AccountIdBackendToCore(value: AccountId): core.AccountId = core.AccountId(value.value)
  implicit def AccountReportIdBackendToCore(value: AccountReportId): core.AccountReportId = core.AccountReportId(value.value)
  implicit def CommentIdBackendToCore(value: CommentId): core.CommentId = core.CommentId(value.value)
  implicit def CommentReportIdBackendToCore(value: CommentReportId): core.CommentReportId = core.CommentReportId(value.value)
  implicit def DeviceIdBackendToCore(value: DeviceId): core.DeviceId = core.DeviceId(value.value)
  implicit def FeedIdBackendToCore(value: backend.FeedId): core.FeedId = core.FeedId(value.value)
  implicit def FeedReportIdBackendToCore(value: backend.FeedReportId): core.FeedReportId = core.FeedReportId(value.value)
  implicit def FriendRequestIdBackendToCore(value: FriendRequestId): core.FriendRequestId = core.FriendRequestId(value.value)
  implicit def GroupIdBackendToCore(value: GroupId): core.GroupId = core.GroupId(value.value)
  implicit def GroupInvitationIdBackendToCore(value: GroupInvitationId): core.GroupInvitationId = core.GroupInvitationId(value.value)
  implicit def GroupReportIdBackendToCore(value: GroupReportId): core.GroupReportId = core.GroupReportId(value.value)
  implicit def MediumIdBackendToCore(value: MediumId): core.MediumId = core.MediumId(value.value)
  implicit def MessageIdBackendToCore(value: MessageId): core.MessageId = core.MessageId(value.value)
  implicit def NotificationIdBackendToCore(value: NotificationId): core.NotificationId = core.NotificationId(value.value)
  implicit def SessionIdBackendToCore(value: SessionId): core.SessionId = core.SessionId(value.value)
  implicit def StampIdBackendToCore(value: StampId): core.StampId = core.StampId(value.value)
  implicit def TimelineFeedIdBackendToCore(value: TimelineFeedId): core.TimelineFeedId = core.TimelineFeedId(value.value)

}




