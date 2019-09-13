package jp.smartreach.members.backend.core.infrastructure.generators

import io.github.cactacea.backend.core.helpers.generators.ValueGenerator
import io.smartreach.members.backend.core.domain.enums._
import jp.smartreach.members.backend.core.infrastructure.identifiers.MemberId
import jp.smartreach.members.backend.core.infrastructure.models._
import org.scalacheck.Gen

import scala.collection.JavaConverters._

trait ModelsGenerator extends ValueGenerator {

  val memberNameMaxLength = 30
  val addressMaxLength = 255
  val cityMaxLength = 11
  val stateMaxLength = 11
  val zipMaxLength = 11

  lazy val emailGen: Gen[String] = for {
    username <- Gen.oneOf("user1", "user2", "user3")
    host <- Gen.oneOf("example1", "example2", "example3")
    domain <- Gen.oneOf(".com", ".net", ".org")
  } yield username + "@" + host + domain

  val telGen = for {
    z <- Gen.const(0)
    n1 <- Gen.listOfN(2, Gen.numChar)
    n2 <- Gen.listOfN(4, Gen.numChar)
    n3 <- Gen.listOfN(4, Gen.numChar)
  } yield { s"$z${n1.mkString}-${n2.mkString}-${n3.mkString}" }

  val zipGen = for {
    n1 <- Gen.listOfN(4, Gen.numChar)
    n2 <- Gen.listOfN(3, Gen.numChar)
  } yield { s"${n1.mkString}-${n2.mkString}" }

  val memberNameGen = Gen.listOfN(memberNameMaxLength, Gen.numChar).map(_.mkString)
  val addressGen = Gen.listOfN(addressMaxLength, Gen.numChar).map(_.mkString)
  val cityGen = Gen.listOfN(cityMaxLength, Gen.numChar).map(_.mkString)
  val stateGen = Gen.listOfN(stateMaxLength, Gen.numChar).map(_.mkString)

  val adjustmentAnswerTypeGen: Gen[AdjustmentAnswerType] = Gen.oneOf(AdjustmentAnswerType.all.asScala)
  val communicationTypeGen: Gen[CommunicationType] = Gen.oneOf(CommunicationType.all.asScala)
  val eventJoinTypeGen: Gen[EventJoinType] = Gen.oneOf(EventJoinType.all.asScala)
  val groupAuthorityTypeGen: Gen[GroupAuthorityType] = Gen.oneOf(GroupAuthorityType.all.asScala)
  val surveyTypeGen: Gen[SurveyType] = Gen.oneOf(SurveyType.all.asScala)
  val taskStatusTypeGen: Gen[TaskStatusType] = Gen.oneOf(TaskStatusType.all.asScala)

  val memberGen: Gen[Members] = for {
    communicationType <- communicationTypeGen
    registeredAt <- currentTimeMillisGen
    email <- Gen.option(emailGen)
    phoneNo <- Gen.option(telGen)
    name <- memberNameGen
    city <- Gen.option(cityGen)
    address <- Gen.option(addressGen)
    zip <- Gen.option(zipGen)
    state <- Gen.option(stateGen)
  } yield (Members(MemberId(0L), name, address, city, state, zip, communicationType, None, email, phoneNo, registeredAt))

}
