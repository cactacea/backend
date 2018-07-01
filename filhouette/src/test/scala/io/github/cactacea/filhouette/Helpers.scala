/**
  * Original work: Silhouette (https://github.com/mohiva/play-silhouette)
  * Modifications Copyright 2015 Mohiva Organisation (license at mohiva dot com)
  *
  * Derivative work: Filhouette (https://github.com/cactacea)
  * Modifications Copyright 2018 Takeshi Shimada
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package io.github.filhouette

import com.twitter.finagle.http.{Fields, HeaderMap, Response, Status}
import com.twitter.util.{Await, Future}
import io.github.cactacea.filhouette.api.AuthInfo
import io.github.cactacea.filhouette.impl.providers.{SocialProfile, SocialStateItem, StatefulAuthInfo}
import io.github.cactacea.filhouette.impl.providers.{SocialStateItem, StatefulAuthInfo}
import org.specs2.execute.{AsResult, Result => Specs2Result}
import org.specs2.matcher.{JsonMatchers, MatchResult}
import org.specs2.mock.Mockito
import org.specs2.mutable.{Around, Specification}

import scala.reflect.ClassTag

/**
 * Executes a before method in the context of the around method.
 */
trait BeforeWithinAround extends Around {
  def before: Any
  abstract override def around[T: AsResult](t: => T): Specs2Result = super.around {
    before; t
  }
}

/**
 * Executes an after method in the context of the around method.
 */
trait AfterWithinAround extends Around {
  def after: Any
  abstract override def around[T: AsResult](t: => T): Specs2Result = super.around {
    try { t } finally { after }
  }
}

/**
 * Executes before and after methods in the context of the around method.
 */
trait BeforeAfterWithinAround extends Around {
  def before: Any
  def after: Any
  abstract override def around[T: AsResult](t: => T): Specs2Result = super.around {
    try { before; t } finally { after }
  }
}

/**
 * Base test case for the social providers.
 */
trait SocialProviderSpec[A <: AuthInfo] extends Specification with Mockito with JsonMatchers {

  /**
    * Extracts the http status code.
    */
  def status(result: Future[Response]): Int = {
    Await.result(result).statusCode
  }

  /**
    * Extracts the Session values set by this Result value.
    */
  def session(of: Future[Response]): HeaderMap = {
    Await.result(of.map(_.headerMap))
  }

  /**
    * Extracts the Location header of this Result value if this Result is a Redirect.
    */
  def redirectLocation(of: Future[Response]): Option[String] = {
    val response = Await.result(of)
    response.status match {
      case Status.Found => response.headerMap.get(Fields.Location)
      case Status.SeeOther => response.headerMap.get(Fields.Location)
      case Status.TemporaryRedirect => response.headerMap.get(Fields.Location)
      case Status.MovedPermanently => response.headerMap.get(Fields.Location)
      case _ => None
    }

  }

  /**
   * Applies a matcher on a simple result.
   *
   * @param providerResult The result from the provider.
   * @param b The matcher block to apply.
   * @return A specs2 match result.
   */
  def result(providerResult: Future[Either[Response, A]])(b: Future[Response] => MatchResult[_]) = {
    Await.result(providerResult) must beLeft[Response].like {
      case result => b(Future.value(result))
    }
  }

  /**
   * Applies a matcher on a auth info.
   *
   * @param providerResult The result from the provider.
   * @param b The matcher block to apply.
   * @return A specs2 match result.
   */
  def authInfo(providerResult: Future[Either[Response, A]])(b: A => MatchResult[_]) = {
    Await.result(providerResult) must beRight[A].like {
      case authInfo => b(authInfo)
    }
  }

  /**
   * Applies a matcher on a social profile.
   *
   * @param providerResult The result from the provider.
   * @param b The matcher block to apply.
   * @return A specs2 match result.
   */
  def profile(providerResult: Future[SocialProfile])(b: SocialProfile => MatchResult[_]) = {
    Await.result(providerResult) must beLike[SocialProfile] {
      case socialProfile => b(socialProfile)
    }
  }

  /**
   * Matches a partial function against a failure message.
   *
   * This method checks if an exception was thrown in a future.
   * @see https://groups.google.com/d/msg/specs2-users/MhJxnvyS1_Q/FgAK-5IIIhUJ
   *
   * @param providerResult The result from the provider.
   * @param f A matcher function.
   * @return A specs2 match result.
   */
  def failed[E <: Throwable: ClassTag](providerResult: Future[_])(f: => PartialFunction[Throwable, MatchResult[_]]) = {
    implicit class Rethrow(t: Throwable) {
      def rethrow = { throw t; t }
    }

    lazy val result = Await.result(providerResult.rescue { case e: Exception => Future.value(e) })

    result must not(throwAn[E])
//    result.rethrow must throwAn[E].like(f)
  }
}

/**
 * Base test case for the social state providers.
 */
trait SocialStateProviderSpec[A <: AuthInfo, S <: SocialStateItem] extends SocialProviderSpec[A] {

  /**
   * Applies a matcher on a simple result.
   *
   * @param providerResult The result from the provider.
   * @param b              The matcher block to apply.
   * @return A specs2 match result.
   */
  def statefulResult(providerResult: Future[Either[Response, StatefulAuthInfo[A, S]]])(
    b: Future[Response] => MatchResult[_]
  ) = {
    Await.result(providerResult) must beLeft[Response].like {
      case result => b(Future.value(result))
    }
  }

  /**
   * Applies a matcher on a stateful auth info.
   *
   * @param providerResult The result from the provider.
   * @param b              The matcher block to apply.
   * @return A specs2 match result.
   */
  def statefulAuthInfo(providerResult: Future[Either[Response, StatefulAuthInfo[A, S]]])(
    b: StatefulAuthInfo[A, S] => MatchResult[_]
  ) = {
    Await.result(providerResult) must beRight[StatefulAuthInfo[A, S]].like {
      case info => b(info)
    }
  }
}

/**
 * Some test-related helper methods.
 */
object Helper {

//  /**
//   * Loads a JSON file from class path.
//   *
//   * @param file The file to load.
//   * @return The JSON value.
//   */
//  def loadJson(file: String): JsonNode = {
//    Option(this.getClass.getResourceAsStream("/" + file.stripPrefix("/"))) match {
//      case Some(is) => Json.parse(Source.fromInputStream(is)(Codec.UTF8).mkString)
//      case None     => throw new Exception("Cannot load file: " + file)
//    }
//  }
}
