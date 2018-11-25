package io.cactacea.finagger

import io.swagger.models.properties.{ArrayProperty, Property, RefProperty}
import io.swagger.models.{ArrayModel, Model, RefModel}

object PropertyUtil {
  def toModel(property: Property): Option[Model] = {
    property match {
      case p: RefProperty =>
        Some(new RefModel(p.getSimpleRef))
      case p: ArrayProperty => {
        val arrayModel = new ArrayModel()
        arrayModel.setItems(p.getItems)
        Some(arrayModel)
      }
      case _ => None
    }
  }
}
