package io.github.cactacea.finagger

import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.util

import io.swagger.converter.{ModelConverter, ModelConverterContext}
import io.swagger.models.Model
import io.swagger.models.properties.Property

class SimpleResolver extends ModelConverter {
  override def resolveProperty(`type`: Type, context: ModelConverterContext, annotations: Array[Annotation], chain: util.Iterator[ModelConverter]): Property = {
    if (chain.hasNext) {
      chain.next().resolveProperty(`type`, context, annotations, chain)
    }
    else {
      null
    }
  }

  override def resolve(`type`: Type, context: ModelConverterContext, chain: util.Iterator[ModelConverter]): Model = {
    chain.next().resolve(`type`,context, chain)
  }
}
