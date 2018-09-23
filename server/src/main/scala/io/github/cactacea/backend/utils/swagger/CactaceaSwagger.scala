package io.github.cactacea.backend.utils.swagger

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.cactacea.finagger.{ParadoxicalWrappedValueModelResolver, Resolvers, WrappedValueModelResolver}
import io.swagger.converter.{ModelConverter, ModelConverters}
import io.swagger.models.Swagger
import io.swagger.scala.converter.SwaggerScalaModelConverter
import io.swagger.util.Json

object CactaceaSwagger extends Swagger {

  swaggerConverters.reverse.foreach(ModelConverters.getInstance().addConverter)

  /**
    * Ordered stack of converters.  First element has highest priority
    */
  protected def swaggerConverters: List[ModelConverter] = List(
    new Resolvers.ParameterzedTypeOption,
    new SwaggerScalaModelConverter,
    new ParadoxicalWrappedValueModelResolver,
    new WrappedValueModelResolver
  ) ++ swaggerTypeOverrides

  /**
    * Custom type overrides to hook in for swager
    */
  protected lazy val swaggerTypeOverrides: List[ModelConverter] = List()

  /**
    * Model property name
    */
  Json.mapper().setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy)

}