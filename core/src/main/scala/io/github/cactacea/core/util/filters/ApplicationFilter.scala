package io.github.cactacea.core.util.filters

import java.util.Locale

import com.google.inject.Singleton
import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.core.util.auth.AuthUserContext
import io.github.cactacea.core.util.tokens.AuthTokenGenerator

import scala.collection.JavaConverters._

@Singleton
class ApplicationFilter extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    AuthTokenGenerator.checkApiKey(request.headerMap.get("X-API-KEY")).flatMap({ _ =>
      val locales = request.headerMap.get(Fields.AcceptLanguage).fold(Seq(Locale.getDefault())) { lang =>
        Locale.LanguageRange.parse(lang).asScala.map(f => Locale.forLanguageTag(f.getRange))
      }
      AuthUserContext.setLocales(request, locales)
      service(request)
    })
  }

}
