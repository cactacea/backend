package io.github.cactacea.backend.core.helpers.generators

import java.util.UUID

import org.scalacheck.Gen

trait DomainValueGenerator extends ValueGenerator {

  // domain value generator
  lazy val hasherGen = Gen.listOfN(30, Gen.alphaChar).map(_.mkString)
  lazy val clientIdGen: Gen[String] = uniqueGen.map(no => s"clientId_${no}")
  lazy val secretOptGen: Gen[String]  = Gen.delay(UUID.randomUUID().toString)
  lazy val pushTokenGen = Gen.option(Gen.delay(UUID.randomUUID().toString))
  lazy val sortedNameGen: Gen[String] = currentTimeNanoMillisGen.map(no => s"${no}")

  // string generator
  lazy val commentMessageGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val channelNameGen: Gen[Option[String]] = Gen.option(Gen.listOfN(1000, Gen.alphaChar).map(_.mkString))
  lazy val messageTextGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val mediumKeyGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val feedMessageTextGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)
  lazy val feedTagGen: Gen[String] = Gen.listOfN(198, Gen.alphaChar).map(_.mkString)
  lazy val reportContentGen: Gen[String] = Gen.listOfN(1000, Gen.alphaChar).map(_.mkString)

  // option string generator
  lazy val messageTextOptGen: Gen[Option[String]] = Gen.option(messageTextGen)
  lazy val reportContentOptGen: Gen[Option[String]] = Gen.option(reportContentGen)

  //
  lazy val uniqueUserNameGen: Gen[String] = for {
    userFirstName <- Gen.listOfN(15, Gen.alphaNumChar).map(_.mkString)
    userLastName <- Gen.listOfN(15, Gen.alphaNumChar).map(_.mkString)
    userName <- uniqueGen.map(no => s"${userFirstName}_${userLastName}_${no}")
  } yield (userName)

  lazy val passwordGen: Gen[String] = for {
    head <- Gen.listOfN(15, Gen.alphaNumChar).map(_.mkString)
    last <- Gen.listOfN(15, Gen.alphaNumChar).map(_.mkString)
    password <- Gen.delay(s"${head}_${last}")
  } yield (password)


  lazy val uniqueDisplayNameGen: Gen[String] = for {
    displayFirstName <- Gen.listOfN(15, Gen.alphaChar).map(_.mkString)
    displayLastName <- Gen.listOfN(15, Gen.alphaChar).map(_.mkString)
    displayName <- uniqueGen.map(no => s"${displayFirstName}_${displayLastName}_${no}")
  }  yield (displayName)

  lazy val uniqueDisplayNameOptGen: Gen[Option[String]] = Gen.option(uniqueDisplayNameGen)

}