package io.github.cactacea.finachat

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

// TODO : Use FinatraObjectMapper instead of following code

object ObjectMapper {

  import com.fasterxml.jackson.databind.{ObjectMapper => JacksonObjectMapper}
  val objectMapper = new JacksonObjectMapper with ScalaObjectMapper
  objectMapper.registerModule(DefaultScalaModule)
  objectMapper.registerModule(SerDeSimpleModule)
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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.twitter.inject.domain.WrappedValue

object WrappedValueSerializer
  extends StdSerializer[WrappedValue[_]](classOf[WrappedValue[_]]) {

  override def serialize(
                          wrappedValue: WrappedValue[_],
                          jgen: JsonGenerator,
                          provider: SerializerProvider
                        ): Unit = {
    jgen.writeObject(wrappedValue.onlyValue)
  }
}

import com.fasterxml.jackson.databind.module.SimpleModule

object SerDeSimpleModule extends SimpleModule {
  addSerializer(WrappedValueSerializer)
}