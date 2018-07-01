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
package io.github.cactacea.filhouette.impl.providers

import com.twitter.finagle.http.{Fields, Request}
import com.twitter.util.{Await, Future}
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.crypto.Base64
import io.github.cactacea.filhouette.api.exceptions.ConfigurationException
import io.github.cactacea.filhouette.api.util.{Credentials, PasswordInfo}
import io.github.cactacea.filhouette.impl.providers.BasicAuthProvider
import io.github.cactacea.filhouette.api.util.PasswordInfo
import io.github.cactacea.filhouette.impl.providers.PasswordProvider._

/**
 * Test case for the [[BasicAuthProvider]] class.
 */
class BasicAuthProviderSpec extends PasswordProviderSpec {

  "The `authenticate` method" should {

    "throw ConfigurationException if unsupported hasher is stored" in new Context {
      val passwordInfo = PasswordInfo("unknown", "hashed(s3cr3t)")
      val loginInfo = LoginInfo(provider.id, credentials.identifier)
      val request = Request()
      request.headerMap.add(Fields.Authorization, encodeCredentials(credentials))

      authInfoRepository.find[PasswordInfo](loginInfo) returns Future.value(Some(passwordInfo))
      Await.result(provider.authenticate(request)) must throwA[ConfigurationException].like {
        case e => e.getMessage must beEqualTo(HasherIsNotRegistered.format(provider.id, "unknown", "foo, bar"))
      }
    }

    "return None if no auth info could be found for the given credentials" in new Context {
      val loginInfo = new LoginInfo(provider.id, credentials.identifier)
      val request = Request()
      request.headerMap.add(Fields.Authorization, encodeCredentials(credentials))

      authInfoRepository.find[PasswordInfo](loginInfo) returns Future.None

      Await.result(provider.authenticate(request)) must beNone
    }

    "return None if password does not match"in new Context {
      val passwordInfo = PasswordInfo("foo", "hashed(s3cr3t)")
      val loginInfo = LoginInfo(provider.id, credentials.identifier)
      val request = Request()
      request.headerMap.add(Fields.Authorization, encodeCredentials(credentials))

      fooHasher.matches(passwordInfo, credentials.password) returns false
      authInfoRepository.find[PasswordInfo](loginInfo) returns Future.value(Some(passwordInfo))

      Await.result(provider.authenticate(request)) must beNone
    }

    "return None if provider isn't responsible"in new Context {
      Await.result(provider.authenticate(Request())) must beNone
    }

    "return None for wrong encoded credentials"in new Context {
      val request = Request()
      request.headerMap.add(Fields.Authorization, "wrong")

      Await.result(provider.authenticate(request)) must beNone
    }

    "return login info if passwords does match"in new Context {
      val passwordInfo = PasswordInfo("foo", "hashed(s3cr3t)")
      val loginInfo = LoginInfo(provider.id, credentials.identifier)
      val request = Request()
      request.headerMap.add(Fields.Authorization, encodeCredentials(credentials))

      fooHasher.matches(passwordInfo, credentials.password) returns true
      authInfoRepository.find[PasswordInfo](loginInfo) returns Future.value(Some(passwordInfo))

      Await.result(provider.authenticate(request)) must beSome(loginInfo)
    }

    "handle a colon in a password"in new Context {
      val credentialsWithColon = Credentials("apollonia.vanova@watchmen.com", "s3c:r3t")
      val passwordInfo = PasswordInfo("foo", "hashed(s3c:r3t)")
      val loginInfo = LoginInfo(provider.id, credentialsWithColon.identifier)
      val request = Request()
      request.headerMap.add(Fields.Authorization, encodeCredentials(credentialsWithColon))

      fooHasher.matches(passwordInfo, credentialsWithColon.password) returns true
      authInfoRepository.find[PasswordInfo](loginInfo) returns Future.value(Some(passwordInfo))

      Await.result(provider.authenticate(request)) must beSome(loginInfo)
    }

    "re-hash password with new hasher if hasher is deprecated"in new Context {
      val passwordInfo = PasswordInfo("bar", "hashed(s3cr3t)")
      val loginInfo = LoginInfo(provider.id, credentials.identifier)
      val request = Request()
      request.headerMap.add(Fields.Authorization, encodeCredentials(credentials))

      fooHasher.hash(credentials.password) returns passwordInfo
      barHasher.matches(passwordInfo, credentials.password) returns true
      authInfoRepository.find[PasswordInfo](loginInfo) returns Future.value(Some(passwordInfo))
      authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo) returns Future.value(passwordInfo)

      Await.result(provider.authenticate(request)) must beSome(loginInfo)
      there was one(authInfoRepository).update(loginInfo, passwordInfo)
    }

    "re-hash password with new hasher if password info is deprecated"in new Context {
      val passwordInfo = PasswordInfo("foo", "hashed(s3cr3t)")
      val loginInfo = LoginInfo(provider.id, credentials.identifier)
      val request = Request()
      request.headerMap.add(Fields.Authorization, encodeCredentials(credentials))

      fooHasher.isDeprecated(passwordInfo) returns Some(true)
      fooHasher.hash(credentials.password) returns passwordInfo
      fooHasher.matches(passwordInfo, credentials.password) returns true
      authInfoRepository.find[PasswordInfo](loginInfo) returns Future.value(Some(passwordInfo))
      authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo) returns Future.value(passwordInfo)

      Await.result(provider.authenticate(request)) must beSome(loginInfo)
      there was one(authInfoRepository).update(loginInfo, passwordInfo)
    }

    "return None if Authorization method is not Basic and Base64 decoded header has ':'"in new Context {
      val request = Request()
      request.headerMap.add(Fields.Authorization, Base64.encode("NotBasic foo:bar"))

      Await.result(provider.authenticate(request)) must beNone
    }
  }

  /**
   * The context.
   */
  trait Context extends BaseContext {

    /**
     * The test credentials.
     */
    lazy val credentials = Credentials("apollonia.vanova@watchmen.com", "s3cr3t")

    /**
     * The provider to test.
     */
    lazy val provider = new BasicAuthProvider(authInfoRepository, passwordHasherRegistry)

    /**
     * Creates the credentials to send within the header.
     *
     * @param credentials The credentials to encode.
     * @return The encoded credentials.
     */
    def encodeCredentials(credentials: Credentials) = {
      "Basic " + Base64.encode(s"${credentials.identifier}:${credentials.password}")
    }
  }
}
