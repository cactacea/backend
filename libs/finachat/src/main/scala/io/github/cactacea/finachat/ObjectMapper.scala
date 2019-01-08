package io.github.cactacea.finachat

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object ObjectMapper {

  import com.fasterxml.jackson.databind.{ObjectMapper => JacksonObjectMapper}
  val objectMapper = new JacksonObjectMapper with ScalaObjectMapper
  objectMapper.registerModule(DefaultScalaModule)
  objectMapper.configOverride(classOf[Option[_]])
    .setIncludeAsProperty(
      JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, Include.ALWAYS))

  def read[T : Manifest](value: String): Either[Exception, T] = {
    try {
      Right(objectMapper.readValue[T](value))
    } catch {
      case e: Exception =>
        Left(e)
    }
  }

  def write(value: AnyRef): String = {
    try {
      val text = objectMapper.writeValueAsString(value)
      text
    } catch {
      case e: Exception =>
        println(e.getMessage)
        "{}"
    }
  }

}
