package io.github.cactacea.addons.aws.storage

import com.typesafe.config.{ConfigFactory, Config => TypeSafeConfig}
import io.github.cactacea.backend.core.util.configs.DurationReader
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

private[aws] object AWSConfig extends DurationReader {

  private val config = ConfigFactory.load().as[TypeSafeConfig]
  private val s3Config = config.as[AWSS3Config]("s3")

  object s3 {
    lazy val awsAccessKeyId = s3Config.bucketName.getOrElse("EKD4SWF0SAENSZ3Z1CFA") // for Minio service
    lazy val awsSecretAccessKey = s3Config.bucketName.getOrElse("n7ynf2m/rheYuPWoLwK2b+pZTGQ7RBZDEhwOBYzx") // for Minio service
    lazy val bucketName = s3Config.bucketName.getOrElse("cactacea")
    lazy val region = s3Config.region.getOrElse("us-east-1")
    lazy val endpoint = s3Config.endpoint
  }

  println("---------------------------------------------------------------")
  println(s"s3.awsAccessKeyId = ${s3.awsAccessKeyId}")
  println(s"s3.awsSecretAccessKey = ${s3.awsSecretAccessKey}")
  println(s"s3.region = ${s3.region}")
  s3.endpoint.foreach({ endpoint =>
    println(s"s3.endpoint = ${endpoint}")
  })
  println(s"s3.bucketName = ${s3.bucketName}")
  println("---------------------------------------------------------------")
  println("")

}
