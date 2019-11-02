package io.github.cactacea.backend.core.helpers.generators

import io.github.cactacea.backend.core.domain.enums._
import org.scalacheck.Gen

trait StatusGenerator {

  val userStatusGen: Gen[UserStatusType] = Gen.oneOf(UserStatusType.values())
  val activeStatusGen: Gen[ActiveStatusType] = Gen.oneOf(ActiveStatusType.values())
  val contentStatusGen: Gen[ContentStatusType] = Gen.oneOf(ContentStatusType.values())
  val deviceTypeGen: Gen[DeviceType] = Gen.oneOf(DeviceType.values())
  val tweetPrivacyTypeGen: Gen[TweetPrivacyType] = Gen.oneOf(TweetPrivacyType.values())
  val tweetPrivacyTypeOptGen: Gen[Option[TweetPrivacyType]]  = Gen.option(tweetPrivacyTypeGen)
  val channelAuthorityTypeGen: Gen[ChannelAuthorityType] = Gen.oneOf(ChannelAuthorityType.values())
  val channelPrivacyTypeGen: Gen[ChannelPrivacyType] = Gen.oneOf(ChannelPrivacyType.values())
  val mediumTypeGen: Gen[MediumType] = Gen.oneOf(MediumType.values())
  val messageTypeGen: Gen[MessageType] = Gen.oneOf(MessageType.values())
  val informationTypeGen: Gen[InformationType] = Gen.oneOf(InformationType.values())
  val notificationTypeGen: Gen[NotificationType] = Gen.oneOf(NotificationType.values())
  val reportTypeGen: Gen[ReportType] = Gen.oneOf(ReportType.values())

}

