package io.github.cactacea.core.util.filters

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.tokens.AuthTokenGenerator

import scala.collection.JavaConverters._

@Singleton
class ApplicationFilter @Inject()(authTokenGenerator: AuthTokenGenerator) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    authTokenGenerator.checkApiKey(request.headerMap.get("X-API-KEY")).flatMap({ _ =>
      val locales = request.headerMap.get(Fields.AcceptLanguage).fold(Seq(Locale.getDefault())) { lang =>
        Locale.LanguageRange.parse(lang).asScala.map(f => Locale.forLanguageTag(f.getRange))
      }
      SessionContext.setLocales(locales)
      service(request)
    })
  }

}
