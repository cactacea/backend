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
package io.github.cactacea.filhouette.api.util

import com.twitter.finagle.http.{MediaType, Request}
import io.github.cactacea.filhouette.impl.util.Json

object RequestExtractor {

  implicit class RichRequest(request: Request) {
    def extractString(name: String): Option[String] = {
      request.params.get(name)
        .orElse(request.headerMap.get(name))
        .orElse(
          request.contentType match {
            case Some(MediaType.Json) => {
              request.contentString.isEmpty match {
                case true =>
                  None
                case false =>
                  val json = Json.obj(request.contentString).get(name)
                  Option(json.asText())
              }
            }
            case _ => None
          })
    }
  }

}

