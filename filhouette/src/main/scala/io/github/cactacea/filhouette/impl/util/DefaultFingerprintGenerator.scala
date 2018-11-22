/**
  * Original work: SecureSocial (https://github.com/jaliss/securesocial)
  * Copyright 2013 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
  *
  * Derivative work: Silhouette (https://github.com/mohiva/play-silhouette)
  * Modifications Copyright 2015 Mohiva Organisation (license at mohiva dot com)
  *
  * Derivative work: Filhouette (https://github.com/cactacea/filhouette)
  * Modifications Copyright 2018 Takeshi Shimada
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

import com.twitter.finagle.http.{Fields, Request}
import io.github.cactacea.filhouette.api.crypto.Hash
import io.github.cactacea.filhouette.api.util.FingerprintGenerator

/**
 * A generator which creates a SHA1 fingerprint from `User-Agent`, `Accept-Language`, `Accept-Charset`
 * and `Accept-Encoding` headers and if defined the remote address of the user.
 *
 * The `Accept` header would also be a good candidate, but this header makes problems in applications
 * which uses content negotiation. So the default fingerprint generator doesn't include it.
 *
 * The same with `Accept-Encoding`. But in Chromium/Blink based browser the content of this header may
 * be changed during requests. @see https://github.com/mohiva/play-silhouette/issues/277
 *
 * @param includeRemoteAddress Indicates if the remote address should be included into the fingerprint.
 */
class DefaultFingerprintGenerator(includeRemoteAddress: Boolean = false) extends FingerprintGenerator {

  /**
   * Generates a fingerprint from request.
   *
   * @param request The request header.
   * @return The generated fingerprint.
   */
  override def generate(implicit request: Request) = {
    Hash.sha1(new StringBuilder()
      .append(request.headerMap.get(Fields.UserAgent).getOrElse("")).append(":")
      .append(request.headerMap.get(Fields.AcceptLanguage).getOrElse("")).append(":")
      .append(request.headerMap.get(Fields.AcceptCharset).getOrElse("")).append(":")
      .append(if (includeRemoteAddress) request.remoteAddress else "")
      .toString()
    )
  }
}
