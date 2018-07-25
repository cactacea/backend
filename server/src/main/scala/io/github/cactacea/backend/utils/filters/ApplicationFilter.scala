package io.github.cactacea.backend.utils.filters

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.utils.auth.{AuthTokenGenerator, SessionContext}

import scala.collection.JavaConverters._

@Singleton
class ApplicationFilter @Inject()(authTokenGenerator: AuthTokenGenerator) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    authTokenGenerator.check(request.headerMap.get("X-API-KEY")).flatMap({ deviceType =>
      val locales = request.headerMap.get(Fields.AcceptLanguage).fold(Seq(Locale.getDefault())) { lang =>
        Locale.LanguageRange.parse(lang).asScala.map(f => Locale.forLanguageTag(f.getRange))
      }
      SessionContext.setLocales(locales)
      SessionContext.setDeviceType(deviceType)
      service(request)
    })
  }

}
