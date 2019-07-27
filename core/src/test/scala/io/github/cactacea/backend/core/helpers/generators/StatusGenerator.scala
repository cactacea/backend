package io.github.cactacea.backend.core.helpers.generators

import io.github.cactacea.backend.core.domain.enums._
import org.scalacheck.Gen

import scala.collection.JavaConverters._

trait StatusGenerator {

  val accountStatusGen: Gen[AccountStatusType] = Gen.oneOf(AccountStatusType.all.asScala)
  val activeStatusGen: Gen[ActiveStatusType] = Gen.oneOf(ActiveStatusType.all.asScala)
  val contentStatusGen: Gen[ContentStatusType] = Gen.oneOf(ContentStatusType.all.asScala)
  val deviceTypeGen: Gen[DeviceType] = Gen.oneOf(DeviceType.all.asScala)
  val feedPrivacyTypeGen: Gen[FeedPrivacyType] = Gen.oneOf(FeedPrivacyType.all.asScala)
  val feedPrivacyTypeOptGen: Gen[Option[FeedPrivacyType]]  = Gen.option(feedPrivacyTypeGen)
  val groupAuthorityTypeGen: Gen[GroupAuthorityType] = Gen.oneOf(GroupAuthorityType.all.asScala)
  val groupPrivacyTypeGen: Gen[GroupPrivacyType] = Gen.oneOf(GroupPrivacyType.all.asScala)
  val mediumTypeGen: Gen[MediumType] = Gen.oneOf(MediumType.all.asScala)
  val messageTypeGen: Gen[MessageType] = Gen.oneOf(MessageType.all.asScala)
  val notificationTypeGen: Gen[NotificationType] = Gen.oneOf(NotificationType.all.asScala)
  val pushNotificationTypeGen: Gen[PushNotificationType] = Gen.oneOf(PushNotificationType.all.asScala)
  val reportTypeGen: Gen[ReportType] = Gen.oneOf(ReportType.all.asScala)

}

