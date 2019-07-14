package io.github.cactacea.backend.core.util.configs

case class CrypterConfig(
                          signerKey: Option[String],
                          crypterKey: Option[String])
