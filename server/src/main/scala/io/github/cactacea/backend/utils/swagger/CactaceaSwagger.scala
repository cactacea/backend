package io.github.cactacea.backend.utils.swagger

import io.cactacea.finagger.{ParadoxicalWrappedValueModelResolver, Resolvers, WrappedValueModelResolver}
import io.swagger.converter.{ModelConverter, ModelConverters}
import io.swagger.models.Swagger
import io.swagger.scala.converter.SwaggerScalaModelConverter

object CactaceaSwagger extends Swagger {

  swaggerConverters.reverse.foreach(ModelConverters.getInstance().addConverter)

  protected def swaggerConverters: List[ModelConverter] = List(
    new Resolvers.ParameterzedTypeOption,
    new SwaggerScalaModelConverter,
    new ParadoxicalWrappedValueModelResolver,
    new WrappedValueModelResolver
  )

}
