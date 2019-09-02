package io.github.cactacea.backend.core.helpers.generators

import java.net.InetAddress
import java.time.Instant
import java.util.UUID

import org.joda.time.DateTime
import org.scalacheck.Gen

import scala.util.hashing.MurmurHash3

trait ValueGenerator {

  lazy val uniqueGen: Gen[Int] = Gen.delay(math.abs(MurmurHash3.productHash((UUID.randomUUID().toString, InetAddress.getLocalHost, Instant.now()))))
  lazy val booleanGen: Gen[Boolean] = Gen.oneOf(true, false)
  lazy val urlGen: Gen[String] = for {
    protocol <- Gen.oneOf("https://")
    name <- Gen.listOfN(10, Gen.alphaChar).map(_.mkString)
    domain <- Gen.oneOf(".com/", ".net/", ".org/")
    path <- Gen.listOfN(2038 - 25, Gen.alphaChar).map(_.mkString)
  } yield protocol + name + domain + path
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