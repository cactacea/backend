package io.github.cactacea.addons.aws.storage

import java.net.URL
import java.util.concurrent.Executors

import awscala.Region0
import awscala.s3.{Bucket, S3}
import com.amazonaws.services.s3.S3ClientOptions
import com.amazonaws.services.s3.model.ObjectMetadata
import com.twitter.concurrent.NamedPoolThreadFactory
import com.twitter.util.{Future, FuturePool}

private[aws] object AWSS3 {

  val fixedThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
    new NamedPoolThreadFactory("QueueFuturePool", makeDaemons = true)
  )
  val futurePool: FuturePool = FuturePool(fixedThreadExecutor)

  private val bucketName = AWSConfig.s3.bucketName
  private val endpoint = AWSConfig.s3.endpoint

  implicit val region = Region0(AWSConfig.s3.region)
  implicit val s3 = {
    val s3 = S3(AWSConfig.s3.awsAccessKeyId, AWSConfig.s3.awsSecretAccessKey)
    endpoint.foreach(s3.setEndpoint(_))
    s3.setS3ClientOptions(
      S3ClientOptions.builder
        .setPathStyleAccess(true)
        .disableChunkedEncoding.build)
    s3
  }

  def upload(contentType: Option[String], key: String, data: Array[Byte]): Future[URL] = {
    futurePool {
      val bucket = Bucket(bucketName)
      val metadata = new ObjectMetadata
      metadata.setContentLength(data.size)
      metadata.setContentType(contentType.getOrElse(""))
      s3.put(bucket = bucket, key = key, bytes = data, metadata = metadata)
      s3.getUrl(bucketName, key)
    }
  }

  def delete(key: String): Future[Unit] = {
    futurePool {
      Bucket(bucketName).get(key).foreach(s3.delete(_))
      ()
    }
  }

}
