package io.github.cactacea.backend.utils.filters

import java.util.Locale

import com.google.inject.Singleton
import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.utils.auth.CactaceaContext

import scala.collection.JavaConverters._

@Singleton
class CactaceaLocaleFilter extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val locales = request.headerMap.get(Fields.AcceptLanguage).fold(Seq(Locale.getDefault())) { lang =>
      Locale.LanguageRange.parse(lang).asScala.map(f => Locale.forLanguageTag(f.getRange))
    }
    CactaceaContext.setLocales(locales)
    service(request)
  }

}
