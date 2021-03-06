package io.github.cactacea.backend.core.helpers.generators

import java.util.UUID

import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType, TweetPrivacyType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import org.scalacheck.Gen

trait ModelsGenerator extends StatusGenerator with DomainValueGenerator {

  // list generator
  lazy val comment20SeqGen = Gen.listOfN(20, commentGen)
  lazy val tweet20SeqGen = Gen.listOfN(20, tweetGen)
  lazy val medium5SeqOptGen: Gen[Option[Seq[Mediums]]] = Gen.option(medium5SeqGen)
  lazy val channel20SeqGen: Gen[Seq[Channels]] = Gen.listOfN(20, channelGen)
  lazy val medium5SeqGen: Gen[Seq[Mediums]] = Gen.listOfN(5, mediumGen)
  lazy val message20SeqGen: Gen[Seq[(Messages, Option[Mediums])]] = for {
    m <- messageGen
    i <- mediumOptGen
    l <- Gen.listOfN(20, (m, i))
  } yield (l)
  lazy val boolean7SeqGen: Gen[Seq[Boolean]] = Gen.listOfN(7, booleanGen)

  // model generator
  lazy val userGen: Gen[Users] = for {
    userName <- uniqueUserNameGen
    displayName <- uniqueDisplayNameGen
    url <- Gen.option(urlGen)
    birthday <- Gen.option(currentTimeMillisGen)
    location <- Gen.option(Gen.alphaStr)
    bio <- Gen.option(Gen.alphaStr)
    userStatus <- userStatusGen
  } yield Users(UserId(0L), userName, displayName, None, None, 0L, 0L, 0L, 0L, url, birthday, location, bio, userStatus, None)

  lazy val user20SeqGen: Gen[Seq[Users]] = for {
    l <- Gen.listOfN(20, userGen)
  } yield (l)

  lazy val sortedUserGen: Gen[Users] = for {
    userName <- currentTimeNanoMillisGen.map(no => s"_${no}")
    displayName <- currentTimeNanoMillisGen.map(no => s"_${no}")
    url <- Gen.option(urlGen)
    birthday <- Gen.option(currentTimeMillisGen)
    location <- Gen.option(Gen.alphaStr)
    bio <- Gen.option(Gen.alphaStr)
    userStatus <- userStatusGen
  } yield Users(UserId(0L), userName, displayName, None, None, 0L, 0L, 0L, 0L, url, birthday, location, bio, userStatus, None)

  lazy val deviceGen: Gen[Devices] = for {
    deviceId <- Gen.const(DeviceId(0L))
    userId <- Gen.const(UserId(0L))
    udid <- Gen.delay(UUID.randomUUID().toString)
    deviceType <- deviceTypeGen
    activeStatus <- activeStatusGen
    pushToken <- pushTokenGen
    userAgent <- Gen.option(Gen.alphaNumStr)
    registeredAt <- currentTimeMillisGen
  } yield Devices(deviceId, userId, udid, deviceType, activeStatus, pushToken, userAgent, registeredAt)


  lazy val channelGen: Gen[Channels] = for {
    channelId <- Gen.const(ChannelId(0L))
    name <- channelNameGen
    privacyType <- channelPrivacyTypeGen
    invitationOnly <- booleanGen
    channelAuthorityType <- channelAuthorityTypeGen
    userId <- Gen.const(UserId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Channels(channelId, name, privacyType, invitationOnly, false, channelAuthorityType, 0L, None, userId, None, organizedAt))

  lazy val organizerChannelGen: Gen[Channels] = for {
    channelId <- Gen.const(ChannelId(0L))
    name <- channelNameGen
    privacyType <- Gen.const(ChannelPrivacyType.everyone)
    invitationOnly <- booleanGen
    channelAuthorityType <- Gen.const(ChannelAuthorityType.organizer)
    userId <- Gen.const(UserId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Channels(channelId, name, privacyType, invitationOnly, false, channelAuthorityType, 0L, None, userId, None, organizedAt))

  lazy val memberChannelGen: Gen[Channels] = for {
    channelId <- Gen.const(ChannelId(0L))
    name <- channelNameGen
    privacyType <- Gen.const(ChannelPrivacyType.everyone)
    invitationOnly <- booleanGen
    channelAuthorityType <- Gen.const(ChannelAuthorityType.member)
    userId <- Gen.const(UserId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Channels(channelId, name, privacyType, invitationOnly, false, channelAuthorityType, 0L, None, userId, None, organizedAt))

  lazy val everyoneChannelGen: Gen[Channels] = for {
    channelId <- Gen.const(ChannelId(0L))
    name <- channelNameGen
    privacyType <- Gen.const(ChannelPrivacyType.everyone)
    invitationOnly <- Gen.const(false)
    channelAuthorityType <- channelAuthorityTypeGen
    userId <- Gen.const(UserId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Channels(channelId, name, privacyType, invitationOnly, false, channelAuthorityType, 0L, None, userId, None, organizedAt))

  lazy val followChannelGen: Gen[Channels] = for {
    channelId <- Gen.const(ChannelId(0L))
    name <- channelNameGen
    privacyType <- Gen.const(ChannelPrivacyType.follows)
    invitationOnly <- booleanGen
    channelAuthorityType <- channelAuthorityTypeGen
    userId <- Gen.const(UserId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Channels(channelId, name, privacyType, invitationOnly, false, channelAuthorityType, 0L, None, userId, None, organizedAt))

  lazy val followerChannelGen: Gen[Channels] = for {
    channelId <- Gen.const(ChannelId(0L))
    name <- channelNameGen
    privacyType <- Gen.const(ChannelPrivacyType.followers)
    invitationOnly <- booleanGen
    channelAuthorityType <- channelAuthorityTypeGen
    userId <- Gen.const(UserId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Channels(channelId, name, privacyType, invitationOnly, false, channelAuthorityType, 0L, None, userId, None, organizedAt))

  lazy val friendChannelGen: Gen[Channels] = for {
    channelId <- Gen.const(ChannelId(0L))
    name <- channelNameGen
    privacyType <- Gen.const(ChannelPrivacyType.friends)
    invitationOnly <- booleanGen
    channelAuthorityType <- channelAuthorityTypeGen
    userId <- Gen.const(UserId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Channels(channelId, name, privacyType, invitationOnly, false, channelAuthorityType, 0L, None, userId, None, organizedAt))

  lazy val textMessageGen: Gen[Messages] = for {
    messageId <- Gen.const(MessageId(0L))
    userId <- Gen.const(UserId(0L))
    channelId <- Gen.const(ChannelId(0L))
    message <- messageTextOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
  } yield (Messages(messageId, userId, channelId, MessageType.text, message, None, None, 0L, 0L, contentWarning, contentStatus, false, postedAt))

  lazy val messageGen: Gen[Messages] = for {
    messageId <- Gen.const(MessageId(0L))
    userId <- Gen.const(UserId(0L))
    channelId <- Gen.const(ChannelId(0L))
    message <- messageTextOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
    messageType = message.fold(MessageType.text)(_ => MessageType.medium)
  } yield (Messages(messageId, userId, channelId, messageType, message, None, None, 0L, 0L, contentWarning, contentStatus, false, postedAt))

  lazy val mediumGen: Gen[Mediums] = for {
    key <- mediumKeyGen
    no <- uniqueGen
    url <- urlGen
    width <- Gen.chooseNum(Int.MinValue, Int.MaxValue)
    height <- Gen.chooseNum(Int.MinValue, Int.MaxValue)
    thumnailUrl <- urlGen
    size <- Gen.chooseNum(Long.MinValue, Long.MaxValue)
    contentStatus <- contentStatusGen
    contentWarning <- booleanGen
    mediumType <- mediumTypeGen
  } yield Mediums(MediumId(0L), s"${key}_${no}", url, width, height, size, Option(thumnailUrl), mediumType, UserId(0L), contentWarning, contentStatus)

  lazy val mediumOptGen = Gen.option(mediumGen)

  lazy val tweetGen: Gen[Tweets] = for {
    message <- tweetMessageTextGen
    tags <- tweetTag5SeqOptGen
    privacyType <- tweetPrivacyTypeGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
  } yield Tweets(TweetId(0L), message, tags, None, None, None, None, None, privacyType, 0L, 0L, UserId(0L), contentWarning, contentStatus, None, false, postedAt)

  lazy val notExpiredTweetGen: Gen[Tweets] = for {
    message <- tweetMessageTextGen
    tags <- tweetTag5SeqOptGen
    privacyType <- tweetPrivacyTypeGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Tweets(TweetId(0L), message, tags, None, None, None, None, None, privacyType, 0L, 0L, UserId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val expiredTweetsGen: Gen[Tweets] = for {
    message <- tweetMessageTextGen
    tags <- tweetTag5SeqOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- passDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Tweets(TweetId(0L), message, tags, None, None, None, None, None, TweetPrivacyType.everyone, 0L, 0L, UserId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val everyoneTweetGen: Gen[Tweets] = for {
    message <- tweetMessageTextGen
    tags <- tweetTag5SeqOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Tweets(TweetId(0L), message, tags, None, None, None, None, None, TweetPrivacyType.everyone, 0L, 0L, UserId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val followerTweetGen: Gen[Tweets] = for {
    message <- tweetMessageTextGen
    tags <- tweetTag5SeqOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Tweets(TweetId(0L), message, tags, None, None, None, None, None, TweetPrivacyType.followers, 0L, 0L, UserId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val selfTweetGen: Gen[Tweets] = for {
    message <- tweetMessageTextGen
    tags <- tweetTag5SeqOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Tweets(TweetId(0L), message, tags, None, None, None, None, None, TweetPrivacyType.self, 0L, 0L, UserId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val friendTweetGen: Gen[Tweets] = for {
    message <- tweetMessageTextGen
    tags <- tweetTag5SeqOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Tweets(TweetId(0L), message, tags, None, None, None, None, None, TweetPrivacyType.friends, 0L, 0L, UserId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val commentGen: Gen[Comments] = for {
    message <- commentMessageGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
  } yield Comments(CommentId(0L), message, TweetId(0L), None, 0L, UserId(0L), contentWarning, contentStatus, false, postedAt)

  lazy val userReportGen: Gen[UserReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield UserReports(UserReportId(0L), UserId(0L), UserId(0L), reportType, reportContent, reportedAt)

  lazy val commentReportGen: Gen[CommentReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield CommentReports(CommentReportId(0L), CommentId(0L), UserId(0L), reportType, reportContent, reportedAt)

  lazy val tweetReportGen: Gen[TweetReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield TweetReports(TweetReportId(0L), TweetId(0L), UserId(0L), reportType, reportContent, reportedAt)

  lazy val channelReportGen: Gen[ChannelReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield ChannelReports(ChannelReportId(0L), ChannelId(0L), UserId(0L), reportType, reportContent, reportedAt)

  lazy val clientGen = for {
    clientId <- clientIdGen
    secret <- secretOptGen
    redirectUri <- urlGen
  } yield (Clients(clientId, Option(secret), redirectUri, None))

}
