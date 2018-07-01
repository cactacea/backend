/**
 * Copyright 2015 Mohiva Organisation (license at mohiva dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.cactacea.filhouette.impl.util

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.twitter.finatra.json.FinatraObjectMapper

/**
 * Provides implementations of utility traits.
 */
package object util {}

object Json {
  private val scalaObjectMapper = new ObjectMapper() with ScalaObjectMapper
  private val jsonFactory = new JsonFactory(scalaObjectMapper)

  scalaObjectMapper.registerModule(DefaultScalaModule)
  scalaObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  val finatraObjectMapper = new FinatraObjectMapper(scalaObjectMapper)

  def validate[T: Manifest](string: String): T = {
    finatraObjectMapper.parse[T](string)
  }

  def validate[T: Manifest](json: JsonNode): T = {
    finatraObjectMapper.parse[T](json)
  }

  def obj(string: String): JsonNode = {
    scalaObjectMapper.readValue(jsonFactory.createParser(string), classOf[JsonNode])
  }

  def obj(fields: (String, Any)*): JsonNode = {
    scalaObjectMapper.valueToTree(fields.toMap)
  }

  def obj(value: Map[Symbol, Any]): JsonNode = {
    scalaObjectMapper.valueToTree(value)
  }

  def toJson(value: Map[Symbol, Any]): String = {
    toJson(value map { case (k,v) => k.name -> v})
  }

  def toJson(value: Any): String = {
    scalaObjectMapper.writeValueAsString(value)
  }

  def toMap[V](json:String)(implicit m: Manifest[V]) = fromJson[Map[String,V]](json)

  def fromJson[T](json: String)(implicit m : Manifest[T]): T = {
    scalaObjectMapper.readValue[T](json)
  }
}

