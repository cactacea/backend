package io.cactacea.finagger

import com.twitter.finagle.http.Status
import io.swagger.models._
import io.swagger.models.parameters._

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._

object FinatraOperation {
  implicit def convertToFinatraOperation(operation: Operation): FinatraOperation = new FinatraOperation(operation)
}

class FinatraOperation(operation: Operation) {

  import FinatraSwagger._

  def routeParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                            (implicit swagger: Swagger): Operation = {
    val param = new PathParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerProperty[T](None))

    operation.parameter(param)

    operation
  }

  def request[T <: Product : TypeTag](implicit swagger: Swagger): Operation = {
    swagger.register[T].foreach(operation.parameter)

    operation
  }

  def queryParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                            (implicit swagger: Swagger): Operation = {
    val param = new QueryParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerProperty[T](None))

    operation.parameter(param)

    operation
  }

  def headerParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                             (implicit swagger: Swagger): Operation = {
    val param = new HeaderParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerProperty[T](None))

    operation.parameter(param)

    operation
  }

  def formParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                           (implicit swagger: Swagger): Operation = {
    val param = new FormParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerProperty[T](None))

    operation.parameter(param)

    operation
  }

  def cookieParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                             (implicit swagger: Swagger): Operation = {
    val param = new CookieParameter()
      .name(name)
      .description(description)
      .required(required)
      .property(swagger.registerProperty[T](None))

    operation.parameter(param)

    operation
  }

  def bodyParam[T: TypeTag](name: String, description: String = "", example: Option[T] = None)
                           (implicit swagger: Swagger): Operation = {
    val schema = swagger.registerProperty[T](example)

    val model = PropertyUtil.toModel(schema)

    val param = new BodyParameter()
      .name(name)
      .description(description)
      .schema(model)

    operation.parameter(param)

    operation
  }

  def responseWith[T: TypeTag](status: Int, description: String = "", example: Option[T] = None)
                              (implicit finatraSwagger: Swagger): Operation = {
    val param =
      if (!(typeOf[T] =:= TypeTag.Unit.tpe)) {
        val ref = finatraSwagger.registerModel[T](example)
        ref match {
          case Some(model) =>
            new Response().description(description).responseSchema(model)
          case None =>
            new Response().description(description)
        }
      } else {
        new Response().description(description)
      }

    operation.response(status, param)

    operation
  }

  def responseWithArray[T: TypeTag](status: Status, example: Array[T])
                                   (implicit finatraSwagger: Swagger): Operation = {
    val param =
      if (!(typeOf[T] =:= TypeTag.Unit.tpe)) {
        val ref = finatraSwagger.registerModel[Array[T]](Some(example))
        ref match {
          case Some(model) =>
            new Response().description(status.reason).responseSchema(model)
          case None =>
            new Response().description(status.reason)
        }
      } else {
        new Response().description(status.reason)
      }

    operation.response(status.code, param)

    operation
  }


  def addSecurity(name: String, scopes: List[String]): Operation = {
    operation.addSecurity(name, scopes.asJava)

    operation
  }

  def tags(tags: List[String]): Operation = {
    operation.setTags(tags.asJava)
    operation
  }
}