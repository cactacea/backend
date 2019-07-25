package io.github.cactacea.backend.addons.aws.storage

private[aws] case class AWSS3Config(
                      awsAccessKeyId: Option[String],
                      awsSecretAccessKey: Option[String],
                      bucketName: Option[String],
                      region: Option[String],
                      endpoint: Option[String]
                        )
