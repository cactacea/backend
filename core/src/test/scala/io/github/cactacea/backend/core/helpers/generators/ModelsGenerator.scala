package io.github.cactacea.backend.core.helpers.generators

import java.net.InetAddress
import java.time.Instant
import java.util.UUID

import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, GroupAuthorityType, GroupPrivacyType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import org.joda.time.DateTime
import org.scalacheck.Gen

import scala.util.hashing.MurmurHash3

trait ModelsGenerator extends StatusGenerator with Generator {

  // value generator
  lazy val uniqueGen: Gen[Int] = Gen.delay(math.abs(MurmurHash3.productHash((UUID.randomUUID().toString, InetAddress.getLocalHost, Instant.now()))))
  lazy val booleanGen: Gen[Boolean] = Gen.oneOf(true, false)
  lazy val urlGen: Gen[String] = for {
    protocol <- Gen.oneOf("https://")
    name <- Gen.listOfN(10, Gen.alphaChar).map(_.mkString)
    domain <- Gen.oneOf(".com/", ".net/", ".org/")
    path <- Gen.listOfN(2038 - 25, Gen.alphaChar).map(_.mkString)
  } yield protocol + name + domain + path
  lazy val passwordGen = Gen.listOfN(255, Gen.alphaChar).map(_.mkString)
  lazy val hasherGen = Gen.listOfN(30, Gen.alphaChar).map(_.mkString)
  lazy val clientIdGen: Gen[String] = uniqueGen.map(no => s"clientId_${no}")
  lazy val secretOptGen: Gen[String]  = Gen.delay(UUID.randomUUID().toString)

  // time generator
  lazy val passDateTimeMillisGen = Gen.choose(DateTime.now().minusWeeks(5).getMillis(), DateTime.now().getMillis)
  lazy val futureDateTimeMillisGen = Gen.choose(DateTime.now().getMillis, DateTime.now().plusWeeks(5).getMillis())
  lazy val currentTimeMillisGen: Gen[Long] = Gen.calendar.map(_.getTimeInMillis)
  lazy val currentTimeNanoMillisGen: Gen[Long] = Gen.delay(System.nanoTime())

  // domain value generator
  lazy val pushTokenGen = Gen.option(Gen.delay(UUID.randomUUID().toString))
  lazy val sortedNameGen: Gen[String] = currentTimeNanoMillisGen.map(no => s"${no}")

  // string generator
  lazy val commentMessageGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val groupNameGen: Gen[Option[String]] = Gen.option(Gen.listOfN(1000, Gen.alphaChar).map(_.mkString))
  lazy val messageTextGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val mediumKeyGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val feedMessageTextGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val feedTagGen: Gen[String] = Gen.listOfN(198, Gen.alphaChar).map(_.mkString)
  lazy val reportContentGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)

  // option string generator
  lazy val messageTextOptGen: Gen[Option[String]] = Gen.option(messageTextGen)
  lazy val reportContentOptGen: Gen[Option[String]] = Gen.option(reportContentGen)

  // list generator
  lazy val comment20ListGen = Gen.listOfN(20, commentGen)
  lazy val feed20ListGen = Gen.listOfN(20, feedGen)
  lazy val medium5ListOptGen: Gen[Option[List[Mediums]]] = Gen.option(medium5ListGen)
  lazy val feedTag5ListOptGen: Gen[Option[String]] = Gen.option(Gen.listOfN(5, feedTagGen).map(_.mkString(" ")))
  lazy val group20ListGen: Gen[List[Groups]] = Gen.listOfN(20, groupGen)
  lazy val medium5ListGen: Gen[List[Mediums]] = Gen.listOfN(5, mediumGen)
  lazy val message20ListGen: Gen[List[(Messages, Option[Mediums])]] = for {
    m <- messagesGen
    i <- mediumOptGen
    l <- Gen.listOfN(20, (m, i))
  } yield (l)
  lazy val boolean7ListGen: Gen[List[Boolean]] = Gen.listOfN(7, booleanGen)

  //
  lazy val uniqueAccountNameGen: Gen[String] = for {
    accountFirstName <- Gen.listOfN(15, Gen.alphaChar).map(_.mkString)
    accountLastName <- Gen.listOfN(15, Gen.alphaChar).map(_.mkString)
    accountName <- uniqueGen.map(no => s"${accountFirstName}_${accountLastName}_${no}")
  } yield (accountName)

  lazy val uniqueDisplayNameGen: Gen[String] = for {
    displayFirstName <- Gen.listOfN(15, Gen.alphaChar).map(_.mkString)
    displayLastName <- Gen.listOfN(15, Gen.alphaChar).map(_.mkString)
    displayName <- uniqueGen.map(no => s"${displayFirstName}_${displayLastName}_${no}")
  }  yield (displayName)

  lazy val uniqueDisplayNameOptGen: Gen[Option[String]] = Gen.option(uniqueDisplayNameGen)

  // model generator
  lazy val accountGen: Gen[Accounts] = for {
    accountName <- uniqueAccountNameGen
    displayName <- uniqueDisplayNameGen
    url <- Gen.option(urlGen)
    birthday <- Gen.option(currentTimeMillisGen)
    location <- Gen.option(Gen.alphaStr)
    bio <- Gen.option(Gen.alphaStr)
    accountStatus <- accountStatusGen
  } yield Accounts(AccountId(0L), accountName, displayName, None, None, 0L, 0L, 0L, 0L, url, birthday, location, bio, accountStatus, None)

  lazy val accounts20ListGen: Gen[List[Accounts]] = for {
    l <- Gen.listOfN(20, accountGen)
  } yield (l)

  lazy val sortedAccountGen: Gen[Accounts] = for {
    accountName <- currentTimeNanoMillisGen.map(no => s"_${no}")
    displayName <- currentTimeNanoMillisGen.map(no => s"_${no}")
    url <- Gen.option(urlGen)
    birthday <- Gen.option(currentTimeMillisGen)
    location <- Gen.option(Gen.alphaStr)
    bio <- Gen.option(Gen.alphaStr)
    accountStatus <- accountStatusGen
  } yield Accounts(AccountId(0L), accountName, displayName, None, None, 0L, 0L, 0L, 0L, url, birthday, location, bio, accountStatus, None)

  lazy val deviceGen: Gen[Devices] = for {
    deviceId <- Gen.const(DeviceId(0L))
    accountId <- Gen.const(AccountId(0L))
    udid <- Gen.delay(UUID.randomUUID().toString)
    deviceType <- deviceTypeGen
    activeStatus <- activeStatusGen
    pushToken <- pushTokenGen
    userAgent <- Gen.option(Gen.alphaNumStr)
    registeredAt <- currentTimeMillisGen
  } yield Devices(deviceId, accountId, udid, deviceType, activeStatus, pushToken, userAgent, registeredAt)


  lazy val groupGen: Gen[Groups] = for {
    groupId <- Gen.const(GroupId(0L))
    name <- groupNameGen
    privacyType <- groupPrivacyTypeGen
    invitationOnly <- booleanGen
    groupAuthorityType <- groupAuthorityTypeGen
    accountId <- Gen.const(AccountId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Groups(groupId, name, privacyType, invitationOnly, false, groupAuthorityType, 0L, None, accountId, None, organizedAt))

  lazy val organizerGroupGen: Gen[Groups] = for {
    groupId <- Gen.const(GroupId(0L))
    name <- groupNameGen
    privacyType <- Gen.const(GroupPrivacyType.everyone)
    invitationOnly <- booleanGen
    groupAuthorityType <- Gen.const(GroupAuthorityType.organizer)
    accountId <- Gen.const(AccountId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Groups(groupId, name, privacyType, invitationOnly, false, groupAuthorityType, 0L, None, accountId, None, organizedAt))

  lazy val memberGroupGen: Gen[Groups] = for {
    groupId <- Gen.const(GroupId(0L))
    name <- groupNameGen
    privacyType <- Gen.const(GroupPrivacyType.everyone)
    invitationOnly <- booleanGen
    groupAuthorityType <- Gen.const(GroupAuthorityType.member)
    accountId <- Gen.const(AccountId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Groups(groupId, name, privacyType, invitationOnly, false, groupAuthorityType, 0L, None, accountId, None, organizedAt))

  lazy val everyoneGroupGen: Gen[Groups] = for {
    groupId <- Gen.const(GroupId(0L))
    name <- groupNameGen
    privacyType <- Gen.const(GroupPrivacyType.everyone)
    invitationOnly <- Gen.const(false)
    groupAuthorityType <- groupAuthorityTypeGen
    accountId <- Gen.const(AccountId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Groups(groupId, name, privacyType, invitationOnly, false, groupAuthorityType, 0L, None, accountId, None, organizedAt))

  lazy val followGroupGen: Gen[Groups] = for {
    groupId <- Gen.const(GroupId(0L))
    name <- groupNameGen
    privacyType <- Gen.const(GroupPrivacyType.follows)
    invitationOnly <- booleanGen
    groupAuthorityType <- groupAuthorityTypeGen
    accountId <- Gen.const(AccountId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Groups(groupId, name, privacyType, invitationOnly, false, groupAuthorityType, 0L, None, accountId, None, organizedAt))

  lazy val followerGroupGen: Gen[Groups] = for {
    groupId <- Gen.const(GroupId(0L))
    name <- groupNameGen
    privacyType <- Gen.const(GroupPrivacyType.followers)
    invitationOnly <- booleanGen
    groupAuthorityType <- groupAuthorityTypeGen
    accountId <- Gen.const(AccountId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Groups(groupId, name, privacyType, invitationOnly, false, groupAuthorityType, 0L, None, accountId, None, organizedAt))

  lazy val friendGroupGen: Gen[Groups] = for {
    groupId <- Gen.const(GroupId(0L))
    name <- groupNameGen
    privacyType <- Gen.const(GroupPrivacyType.friends)
    invitationOnly <- booleanGen
    groupAuthorityType <- groupAuthorityTypeGen
    accountId <- Gen.const(AccountId(0L))
    organizedAt <- currentTimeMillisGen
  } yield (Groups(groupId, name, privacyType, invitationOnly, false, groupAuthorityType, 0L, None, accountId, None, organizedAt))

  lazy val textMessageGen: Gen[Messages] = for {
    messageId <- Gen.const(MessageId(0L))
    accountId <- Gen.const(AccountId(0L))
    groupId <- Gen.const(GroupId(0L))
    message <- messageTextOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
  } yield (Messages(messageId, accountId, groupId, MessageType.text, message, None, None, 0L, 0L, contentWarning, contentStatus, false, postedAt))

  lazy val messagesGen: Gen[Messages] = for {
    messageId <- Gen.const(MessageId(0L))
    accountId <- Gen.const(AccountId(0L))
    groupId <- Gen.const(GroupId(0L))
    message <- messageTextOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
    messageType = message.fold(MessageType.text)(_ => MessageType.medium)
  } yield (Messages(messageId, accountId, groupId, messageType, message, None, None, 0L, 0L, contentWarning, contentStatus, false, postedAt))

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
  } yield Mediums(MediumId(0L), s"${key}_${no}", url, width, height, size, Option(thumnailUrl), mediumType, AccountId(0L), contentWarning, contentStatus)

  lazy val mediumOptGen = Gen.option(mediumGen)

  lazy val feedGen: Gen[Feeds] = for {
    message <- feedMessageTextGen
    tags <- feedTag5ListOptGen
    privacyType <- feedPrivacyTypeGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
  } yield Feeds(FeedId(0L), message, tags, None, None, None, None, None, privacyType, 0L, 0L, AccountId(0L), contentWarning, contentStatus, None, false, postedAt)

  lazy val notExpiredFeedGen: Gen[Feeds] = for {
    message <- feedMessageTextGen
    tags <- feedTag5ListOptGen
    privacyType <- feedPrivacyTypeGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Feeds(FeedId(0L), message, tags, None, None, None, None, None, privacyType, 0L, 0L, AccountId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val expiredFeedsGen: Gen[Feeds] = for {
    message <- feedMessageTextGen
    tags <- feedTag5ListOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- passDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Feeds(FeedId(0L), message, tags, None, None, None, None, None, FeedPrivacyType.everyone, 0L, 0L, AccountId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val everyoneFeedGen: Gen[Feeds] = for {
    message <- feedMessageTextGen
    tags <- feedTag5ListOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Feeds(FeedId(0L), message, tags, None, None, None, None, None, FeedPrivacyType.everyone, 0L, 0L, AccountId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val followerFeedGen: Gen[Feeds] = for {
    message <- feedMessageTextGen
    tags <- feedTag5ListOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Feeds(FeedId(0L), message, tags, None, None, None, None, None, FeedPrivacyType.followers, 0L, 0L, AccountId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val selfFeedGen: Gen[Feeds] = for {
    message <- feedMessageTextGen
    tags <- feedTag5ListOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Feeds(FeedId(0L), message, tags, None, None, None, None, None, FeedPrivacyType.self, 0L, 0L, AccountId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val friendFeedGen: Gen[Feeds] = for {
    message <- feedMessageTextGen
    tags <- feedTag5ListOptGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    expiration <- futureDateTimeMillisGen
    postedAt <- currentTimeMillisGen
  } yield Feeds(FeedId(0L), message, tags, None, None, None, None, None, FeedPrivacyType.friends, 0L, 0L, AccountId(0L), contentWarning, contentStatus, Option(expiration), false, postedAt)

  lazy val commentGen: Gen[Comments] = for {
    message <- commentMessageGen
    contentWarning <- booleanGen
    contentStatus <- contentStatusGen
    postedAt <- currentTimeMillisGen
  } yield Comments(CommentId(0L), message, FeedId(0L), None, 0L, AccountId(0L), contentWarning, contentStatus, false, postedAt)

  lazy val accountReportGen: Gen[AccountReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield AccountReports(AccountReportId(0L), AccountId(0L), AccountId(0L), reportType, reportContent, reportedAt)

  lazy val commentReportGen: Gen[CommentReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield CommentReports(CommentReportId(0L), CommentId(0L), AccountId(0L), reportType, reportContent, reportedAt)

  lazy val feedReportGen: Gen[FeedReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield FeedReports(FeedReportId(0L), FeedId(0L), AccountId(0L), reportType, reportContent, reportedAt)

  lazy val groupReportGen: Gen[GroupReports] = for {
    reportType <- reportTypeGen
    reportContent <- reportContentOptGen
    reportedAt <- currentTimeMillisGen
  } yield GroupReports(GroupReportId(0L), GroupId(0L), AccountId(0L), reportType, reportContent, reportedAt)

  lazy val authenticationGen: Gen[Authentications] = for {
    providerId <- Gen.listOfN(30, Gen.alphaChar).map(_.mkString)
    providerKey <- uniqueGen.map(no => s"provider_key_${no}")
    password <- passwordGen
    hasher <-  hasherGen
    confirm <- booleanGen
    accountId <- Gen.option(AccountId(0L))
  } yield (Authentications(providerId, providerKey, password, hasher, confirm, accountId))


  lazy val clientGen = for {
    clientId <- clientIdGen
    secret <- secretOptGen
    redirectUri <- urlGen
  } yield (Clients(clientId, Option(secret), redirectUri, None))

}
