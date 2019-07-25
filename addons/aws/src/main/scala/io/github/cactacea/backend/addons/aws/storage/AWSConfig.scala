package io.github.cactacea.backend.addons.aws.storage

import com.typesafe.config.{ConfigFactory, Config => TypeSafeConfig}
import io.github.cactacea.backend.core.util.configs.DurationReader
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

private[aws] object AWSConfig extends DurationReader {

  private val config = ConfigFactory.load().as[TypeSafeConfig]
  private val s3Config = config.as[AWSS3Config]("s3")

  object s3 { // scalastyle:ignore
    lazy val awsAccessKeyId = s3Config.awsAccessKeyId.getOrElse("")
    lazy val awsSecretAccessKey = s3Config.awsSecretAccessKey.getOrElse("")
    lazy val bucketName = s3Config.bucketName.getOrElse("cactacea")
    lazy val region = s3Config.region.getOrElse("us-east-1")
    lazy val endpoint = s3Config.endpoint.getOrElse("http://localhost:4572")
  }

  println("---------------------------------------------------------------")
  println(s"s3.awsAccessKeyId = ${s3.awsAccessKeyId}")
  println(s"s3.awsSecretAccessKey = ${s3.awsSecretAccessKey}")
  println(s"s3.region = ${s3.region}")
  println(s"s3.endpoint = ${s3.endpoint}")
  println(s"s3.bucketName = ${s3.bucketName}")
  println("---------------------------------------------------------------")
  println("")

}
