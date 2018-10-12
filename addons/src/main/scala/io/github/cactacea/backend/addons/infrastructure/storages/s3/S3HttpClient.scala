package io.github.cactacea.backend.addons.infrastructure.storages.s3

import awscala.s3.{Bucket, PutObjectResult, S3}
import awscala.{Region, Region0}
import com.amazonaws.services.s3.S3ClientOptions
import com.amazonaws.services.s3.model.ObjectMetadata
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.backend.addons.configs.Config

class S3HttpClient {

  private val bucketName = Config.s3.bucketName
  private val endpoint = Config.s3.endpoint

  implicit val region = Region0.Tokyo
  implicit val s3 = {
    val s3 = S3.at(Region.Tokyo)
    s3.setEndpoint(endpoint)
    s3.setS3ClientOptions(
      S3ClientOptions.builder
        .setPathStyleAccess(true)
        .disableChunkedEncoding.build)
    s3
  }

  def put(contentType: Option[String], data: Array[Byte]): Future[(PutObjectResult, String)] = {
    FuturePool.unboundedPool {
      val bucket = Bucket(bucketName)
      val key = java.util.UUID.randomUUID().toString
      val metadata = new ObjectMetadata
      metadata.setContentLength(data.size)
      metadata.setContentType(contentType.getOrElse(""))
      val result = s3.put(bucket = bucket, key = key, bytes = data, metadata = metadata)
      val filePath = endpoint + "/" + bucketName + "/" + key
      (result, filePath)
    }
  }

  def delete(key: String): Future[Boolean] = {
    FuturePool.unboundedPool {
      Bucket(bucketName).get(key).foreach(s3.delete(_))
      true
    }
  }

}
