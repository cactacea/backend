package io.github.cactacea.backend.core.util.responses

import com.twitter.finagle.http.Status
import io.swagger.models.ModelImpl
import io.swagger.models.properties.{LongProperty, StringProperty}

trait CactaceaError {

  val code: Int
  val message: String

  def status: Status

}

object CactaceaError {

  def swaggerModel = {
    val code = new LongProperty().description("code")
    val message = new StringProperty().description("message")
    code.setRequired(true)
    message.setRequired(true)
    new ModelImpl().name("CactaceaError").`type`("object")
      .property("code", code)
      .property("message", message)
  }

}