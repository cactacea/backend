package com.jakehschwartz.finatra.swagger

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
                        ) {
    jgen.writeObject(wrappedValue.onlyValue)
  }
}

import com.fasterxml.jackson.databind.module.SimpleModule

object WrappedValueModule extends SimpleModule {
  addSerializer(WrappedValueSerializer)
}