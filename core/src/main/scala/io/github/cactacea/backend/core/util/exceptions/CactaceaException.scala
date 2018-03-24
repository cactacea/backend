package io.github.cactacea.backend.core.util.exceptions

import io.github.cactacea.backend.core.util.responses.CactaceaError

case class CactaceaException(val error: CactaceaError) extends scala.Exception with scala.Product with scala.Serializable {
  override def getMessage()  = {
    error.toString()
  }
}
