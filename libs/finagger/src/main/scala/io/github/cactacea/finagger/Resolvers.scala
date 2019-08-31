package io.github.cactacea.finagger

import java.lang.annotation.Annotation
import java.lang.reflect.{ParameterizedType, Type}
import java.util

import io.swagger.converter.{ModelConverter, ModelConverterContext}
import io.swagger.models.properties.Property

object Resolvers {
  class ParameterzedTypeOption extends SimpleResolver {
    override def resolveProperty(`type`: Type, context: ModelConverterContext, annotations: Array[Annotation], chain: util.Iterator[ModelConverter]): Property = {
      `type` match {
        case parameterizedType: ParameterizedType if parameterizedType.getRawType == classOf[Option[_]] =>
          val prop = chain.next().resolveProperty(parameterizedType.getActualTypeArguments.head, context, annotations, chain)

          prop.setRequired(false)

          return prop
        case _ =>
      }

      super.resolveProperty(`type`, context, annotations, chain)
    }
  }
}
