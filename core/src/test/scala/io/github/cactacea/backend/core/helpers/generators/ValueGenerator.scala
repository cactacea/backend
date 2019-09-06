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

  // time generator
  lazy val passDateTimeMillisGen = Gen.choose(DateTime.now().minusWeeks(5).getMillis(), DateTime.now().getMillis)
  lazy val futureDateTimeMillisGen = Gen.choose(DateTime.now().getMillis, DateTime.now().plusWeeks(5).getMillis())
  lazy val currentTimeMillisGen: Gen[Long] = Gen.calendar.map(_.getTimeInMillis)
  lazy val currentTimeNanoMillisGen: Gen[Long] = Gen.delay(System.nanoTime())

}