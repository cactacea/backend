package io.github.cactacea.backend.addons.configs

case class S3Config (
                      awsAccessKeyId: Option[String],
                      awsSecretAccessKey: Option[String],
                      bucketName: Option[String],
                      endpoint: Option[String]
                        )
