package io.cactacea.finagger

import io.swagger.models.properties.{ArrayProperty, Property, RefProperty}
import io.swagger.models.{ArrayModel, Model, RefModel}

object PropertyUtil {
  def toModel(property: Property): Model = {
    property match {
      case null => null
      case p: RefProperty => new RefModel(p.getSimpleRef)
      case p: ArrayProperty => {
        val arrayModel = new ArrayModel()
        arrayModel.setItems(p.getItems)
        arrayModel
      }
      case _ => null
    }
  }
}
