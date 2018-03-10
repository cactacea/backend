package com.jakehschwartz.finatra.swagger

import io.swagger.models.properties.{ArrayProperty, Property, RefProperty}
import io.swagger.models.{ArrayModel, Model, RefModel}

object SchemaUtil {
  class SwaggerProperty(p: Property) {
    def toModel: Model = {
      val model = p match {
        case null => null
        case p: RefProperty => new RefModel(p.getSimpleRef)
        case p: ArrayProperty =>
          val arrayModel = new ArrayModel()
          arrayModel.setItems(p.getItems)
          arrayModel
        case _ => null
      }
      model
    }
  }

  implicit def swaggerProperty(p: Property): SwaggerProperty = new SwaggerProperty(p)
}
